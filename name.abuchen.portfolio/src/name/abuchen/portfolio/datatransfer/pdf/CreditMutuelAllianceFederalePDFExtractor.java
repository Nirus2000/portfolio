package name.abuchen.portfolio.datatransfer.pdf;

import name.abuchen.portfolio.datatransfer.ExtractorUtils;
import name.abuchen.portfolio.datatransfer.pdf.PDFParser.Block;
import name.abuchen.portfolio.datatransfer.pdf.PDFParser.DocumentType;
import name.abuchen.portfolio.datatransfer.pdf.PDFParser.Transaction;
import name.abuchen.portfolio.model.BuySellEntry;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.PortfolioTransaction;
import name.abuchen.portfolio.money.Values;

@SuppressWarnings("nls")
public class CreditMutuelAllianceFederalePDFExtractor extends AbstractPDFExtractor
{
    public CreditMutuelAllianceFederalePDFExtractor(Client client)
    {
        super(client);

        addBankIdentifier("Suravenir");

        addBuySellTransaction();
        addExchangeSaleTransaction();
        addExchangeBuyTransaction();
    }

    @Override
    public String getLabel()
    {
        return "Crédit Mutuel Alliance Fédérale (Suravenir)";
    }

    private void addBuySellTransaction()
    {
        final DocumentType type = new DocumentType("Objet : Confirmation de versement");
        this.addDocumentTyp(type);

        Transaction<BuySellEntry> pdfTransaction = new Transaction<>();

        Block firstRelevantLine = new Block("^Objet : Confirmation de versement$", "^Total.*$");
        type.addBlock(firstRelevantLine);
        firstRelevantLine.set(pdfTransaction);

        pdfTransaction //

                        .subject(() -> {
                            BuySellEntry portfolioTransaction = new BuySellEntry();
                            portfolioTransaction.setType(PortfolioTransaction.Type.BUY);
                            return portfolioTransaction;
                        })

                        // @formatter:off
                        // Montant brut (frais inclus) : 3 000,00 €
                        // LYXOR STOXX50 DR UCITS ETF DIST FR0007054358 05/06/2020 33,26 90,1984 3 000,00
                        // @formatter:on
                        .section("currency", "name", "isin") //
                        .match("^Montant brut \\(frais inclus\\) : [\\,\\d\\s]+ (?<currency>\\p{Sc})$") //
                        .match("^(?<name>.*) (?<isin>[A-Z]{2}[A-Z0-9]{9}[0-9]) [\\d]{2}\\/[\\d]{2}\\/[\\d]{4}.*") //
                        .assign((t, v) -> t.setSecurity(getOrCreateSecurity(v)))

                        // @formatter:off
                        // LYXOR STOXX50 DR UCITS ETF DIST FR0007054358 05/06/2020 33,26 90,1984 3 000,00
                        // @formatter:on
                        .section("shares") //
                        .match("^.* [A-Z]{2}[A-Z0-9]{9}[0-9] [\\d]{2}\\/[\\d]{2}\\/[\\d]{4} [\\d\\s]+,[\\d]{2} (?<shares>[\\d\\s]+,[\\d]{1,}) [\\d\\s]+,[\\d]{2}$") //
                        .assign((t, v) -> t.setShares(asShares(v.get("shares"))))

                        // @formatter:off
                        // ü Date du versement : 04/06/2020
                        // @formatter:on
                        .section("date") //
                        .match("^.*Date du versement : (?<date>[\\d]{2}\\/[\\d]{2}\\/[\\d]{4})$") //
                        .assign((t, v) -> t.setDate(asDate(v.get("date"))))

                        // @formatter:off
                        // Montant net : 3 000,00 €
                        // @formatter:on
                        .section("amount", "currency") //
                        .match("^Montant net : (?<amount>[\\,\\d\\s]+) (?<currency>\\p{Sc})$") //
                        .assign((t, v) -> {
                            t.setCurrencyCode(asCurrencyCode(v.get("currency")));
                            t.setAmount(asAmount(v.get("amount")));
                        })

                        .wrap(BuySellEntryItem::new);
    }

    private void addExchangeSaleTransaction()
    {
        final DocumentType type = new DocumentType("Objet : Confirmation d.arbitrage");
        this.addDocumentTyp(type);

        Transaction<BuySellEntry> pdfTransaction = new Transaction<>();

        Block firstRelevantLine = new Block("^Objet : Confirmation d.arbitrage$");
        type.addBlock(firstRelevantLine);
        firstRelevantLine.set(pdfTransaction);

        pdfTransaction //

                        .subject(() -> {
                            BuySellEntry portfolioTransaction = new BuySellEntry();
                            portfolioTransaction.setType(PortfolioTransaction.Type.SELL);
                            return portfolioTransaction;
                        })

                        .oneOf( //
                                        // @formatter:off
                                        // Montant brut de l’arbitrage : 4 027,53 €
                                        // Compartiment en gestion libre
                                        // DNCA FINANCE
                                        // CENTIFOLIA C FR0007076930 16/02/2021 324,32 6,2804 2 036,86
                                        // @formatter:on
                                        section -> section //
                                                        .attributes("currency", "name", "isin") //
                                                        .match("^Montant brut de l.arbitrage : [\\,\\d\\s]+ (?<currency>\\p{Sc})$") //
                                                        .find("Compartiment en gestion libre") //
                                                        .match("^(?<name>.*) (?<isin>[A-Z]{2}[A-Z0-9]{9}[0-9]) [\\d]{2}\\/[\\d]{2}\\/[\\d]{4}.*") //
                                                        .find("Compartiment en gestion libre") //
                                                        .assign((t, v) -> t.setSecurity(getOrCreateSecurity(v))),
                                        // @formatter:off
                                        // Montant brut de l’arbitrage : 143,05 €
                                        // Compartiment en gestion libre
                                        // SURAVENIR
                                        // FDS EUROS SURAVENIR RENDEMENT 11/02/2025 1,00 143,0767 143,08
                                        // Compartiment en gestion libre
                                        // @formatter:on
                                        section -> section //
                                                        .attributes("currency", "name") //
                                                        .match("^Montant brut de l.arbitrage : [\\,\\d\\s]+ (?<currency>\\p{Sc})$") //
                                                        .find("Compartiment en gestion libre") //
                                                        .match("^(?<name>.*) [\\d]{2}\\/[\\d]{2}\\/[\\d]{4}.*") //
                                                        .find("Compartiment en gestion libre") //
                                                        .assign((t, v) -> t.setSecurity(getOrCreateSecurity(v))))

                        // @formatter:off
                        // FDS EUROS SURAVENIR RENDEMENT 11/02/2025 1,00 143,0767 143,08
                        // Compartiment en gestion libre
                        // @formatter:on
                        .section("shares") //
                        .match("^.* [\\d]{2}\\/[\\d]{2}\\/[\\d]{4} [\\d\\s]+,[\\d]{2} (?<shares>[\\d\\s]+,[\\d]{1,}) [\\d\\s]+,[\\d]{2}$") //
                        .find("Compartiment en gestion libre") //
                        .assign((t, v) -> t.setShares(asShares(v.get("shares"))))

                        // @formatter:off
                        // l Date de l’arbitrage 09 février 2025
                        // @formatter:on
                        .section("date") //
                        .match("^.*Date de l.arbitrage (?<date>[\\d]{2} .* [\\d]{4})$") //
                        .assign((t, v) -> t.setDate(asDate(v.get("date"))))

                        // @formatter:off
                        // Montant net : 142,95 €
                        // @formatter:on
                        .section("amount", "currency") //
                        .match("^Montant net : (?<amount>[\\,\\d\\s]+) (?<currency>\\p{Sc})$") //
                        .assign((t, v) -> {
                            t.setCurrencyCode(asCurrencyCode(v.get("currency")));
                            t.setAmount(asAmount(v.get("amount")));
                        })

                        .wrap(BuySellEntryItem::new);
        
        addFeesSectionsTransaction(pdfTransaction, type);
    }

    private void addExchangeBuyTransaction()
    {
        final DocumentType type = new DocumentType("Objet : Confirmation d.arbitrage");
        this.addDocumentTyp(type);

        Transaction<BuySellEntry> pdfTransaction = new Transaction<>();

        Block firstRelevantLine = new Block("^Objet : Confirmation d.arbitrage$");
        type.addBlock(firstRelevantLine);
        firstRelevantLine.set(pdfTransaction);

        pdfTransaction //

                        .subject(() -> {
                            BuySellEntry portfolioTransaction = new BuySellEntry();
                            portfolioTransaction.setType(PortfolioTransaction.Type.BUY);
                            return portfolioTransaction;
                        })

                        // @formatter:off
                        // Montant brut de l’arbitrage : 143,05 €
                        // Compartiment en gestion libre
                        // AMUNDI
                        // AMUNDI MSCI WORLD II UCITS ETF D FR0010315770 11/02/2025 369,52 0,3869 142,95
                        // @formatter:on
                        .section("currency", "name", "isin") //
                        .match("^Montant brut de l.arbitrage : [\\,\\d\\s]+ (?<currency>\\p{Sc})$") //
                        .find("Compartiment en gestion libre") //
                        .match("^(?<name>.*) (?<isin>[A-Z]{2}[A-Z0-9]{9}[0-9]) [\\d]{2}\\/[\\d]{2}\\/[\\d]{4}.*") //
                        .assign((t, v) -> t.setSecurity(getOrCreateSecurity(v)))

                        // @formatter:off
                        // Compartiment en gestion libre
                        // AMUNDI
                        // AMUNDI MSCI WORLD II UCITS ETF D FR0010315770 11/02/2025 369,52 0,3869 142,95
                        // @formatter:on
                        .section("shares") //
                        .find("Compartiment en gestion libre") //
                        .match("^.* [A-Z]{2}[A-Z0-9]{9}[0-9] [\\d]{2}\\/[\\d]{2}\\/[\\d]{4} (?<shares>[\\d\\s]+,[\\d]{1,}) [\\d\\s]+,[\\d]{1,} [\\d\\s]+,[\\d]{2}$") //
                        .assign((t, v) -> t.setShares(asShares(v.get("shares"))))

                        // @formatter:off
                        // Compartiment en gestion libre
                        // AMUNDI
                        // AMUNDI MSCI WORLD II UCITS ETF D FR0010315770 11/02/2025 369,52 0,3869 142,95
                        // @formatter:on
                        .section("date") //
                        .find("Compartiment en gestion libre") //
                        .match("^.* [A-Z]{2}[A-Z0-9]{9}[0-9] (?<date>[\\d]{2}\\/[\\d]{2}\\/[\\d]{4}) [\\d\\s]+,[\\d]{1,} [\\d\\s]+,[\\d]{1,} [\\d\\s]+,[\\d]{2}$") //
                        .assign((t, v) -> t.setDate(asDate(v.get("date"))))

                        // @formatter:off
                        // Montant net : 142,95 €
                        // @formatter:on
                        .section("amount", "currency") //
                        .match("^Montant net : (?<amount>[\\,\\d\\s]+) (?<currency>\\p{Sc})$") //
                        .assign((t, v) -> {
                            t.setCurrencyCode(asCurrencyCode(v.get("currency")));
                            t.setAmount(asAmount(v.get("amount")));
                        })

                        .wrap(BuySellEntryItem::new);
    }

    private <T extends Transaction<?>> void addFeesSectionsTransaction(T transaction, DocumentType type)
    {
        transaction //

                        // @formatter:off
                        // Frais de gestion (*) : 0,10 €
                        // @formatter:on
                        .section("currency", "fee").optional() //
                        .match("^Frais de gestion.* (?<fee>[\\.,\\d]+) (?<currency>\\p{Sc})$") //
                        .assign((t, v) -> processFeeEntries(t, v, type));
    }

    @Override
    protected long asAmount(String value)
    {
        return ExtractorUtils.convertToNumberLong(value, Values.Amount, "fr", "FR");
    }

    @Override
    protected long asShares(String value)
    {
        return ExtractorUtils.convertToNumberLong(value, Values.Share, "fr", "FR");
    }
}
