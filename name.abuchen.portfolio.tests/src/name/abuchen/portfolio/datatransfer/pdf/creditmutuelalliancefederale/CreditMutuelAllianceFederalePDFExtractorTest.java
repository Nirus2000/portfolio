package name.abuchen.portfolio.datatransfer.pdf.creditmutuelalliancefederale;

import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasAmount;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasCurrencyCode;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasDate;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasFees;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasGrossValue;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasIsin;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasName;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasNote;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasShares;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasSource;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasTaxes;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasTicker;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.hasWkn;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.purchase;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.sale;
import static name.abuchen.portfolio.datatransfer.ExtractorMatchers.security;
import static name.abuchen.portfolio.datatransfer.ExtractorTestUtilities.countAccountTransactions;
import static name.abuchen.portfolio.datatransfer.ExtractorTestUtilities.countBuySell;
import static name.abuchen.portfolio.datatransfer.ExtractorTestUtilities.countSecurities;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import name.abuchen.portfolio.datatransfer.Extractor.Item;
import name.abuchen.portfolio.datatransfer.actions.AssertImportActions;
import name.abuchen.portfolio.datatransfer.pdf.CreditMutuelAllianceFederalePDFExtractor;
import name.abuchen.portfolio.datatransfer.pdf.PDFInputFile;
import name.abuchen.portfolio.model.Client;

@SuppressWarnings("nls")
public class CreditMutuelAllianceFederalePDFExtractorTest
{
    @Test
    public void testCompteAChat01()
    {
        CreditMutuelAllianceFederalePDFExtractor extractor = new CreditMutuelAllianceFederalePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "CompteAChat01.txt"), errors);

        assertThat(errors, empty());
        assertThat(countSecurities(results), is(1L));
        assertThat(countBuySell(results), is(1L));
        assertThat(countAccountTransactions(results), is(0L));
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "EUR");

        // check security
        assertThat(results, hasItem(security( //
                        hasIsin("FR0007054358"), hasWkn(null), hasTicker(null), //
                        hasName("LYXOR STOXX50 DR UCITS ETF DIST"), //
                        hasCurrencyCode("EUR"))));

        // check buy sell transaction
        assertThat(results, hasItem(purchase( //
                        hasDate("2020-06-04T00:00"), hasShares(90.1984), //
                        hasSource("CompteAChat01.txt"), //
                        hasNote(null), //
                        hasAmount("EUR", 3000.00), hasGrossValue("EUR", 3000.00), //
                        hasTaxes("EUR", 0.00), hasFees("EUR", 0.00))));
    }

    @Test
    public void testCompteExchangeSaleBuy01()
    {
        CreditMutuelAllianceFederalePDFExtractor extractor = new CreditMutuelAllianceFederalePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "ExchangeSaleBuy01.txt"), errors);

        assertThat(errors, empty());
        assertThat(countSecurities(results), is(2L));
        assertThat(countBuySell(results), is(2L));
        assertThat(countAccountTransactions(results), is(0L));
        assertThat(results.size(), is(4));
        new AssertImportActions().check(results, "EUR");

        // check security
        assertThat(results, hasItem(security( //
                        hasIsin(null), hasWkn(null), hasTicker(null), //
                        hasName("FDS EUROS SURAVENIR RENDEMENT"), //
                        hasCurrencyCode("EUR"))));

        // check security
        assertThat(results, hasItem(security( //
                        hasIsin(null), hasWkn(null), hasTicker(null), //
                        hasName("FDS EUROS SURAVENIR RENDEMENT"), //
                        hasCurrencyCode("EUR"))));

        // check buy sell transaction
        assertThat(results, hasItem(sale( //
                        hasDate("2025-02-09T00:00"), hasShares(143.0767), //
                        hasSource("ExchangeSaleBuy01.txt"), //
                        hasNote(null), //
                        hasAmount("EUR", 142.95), hasGrossValue("EUR", 143.05), //
                        hasTaxes("EUR", 0.00), hasFees("EUR", 0.10))));

        // check buy sell transaction
        assertThat(results, hasItem(purchase( //
                        hasDate("2025-02-11T00:00"), hasShares(369.52), //
                        hasSource("ExchangeSaleBuy01.txt"), //
                        hasNote(null), //
                        hasAmount("EUR", 142.95), hasGrossValue("EUR", 142.95), //
                        hasTaxes("EUR", 0.00), hasFees("EUR", 0.00))));
    }

    @Test
    public void testCompteExchangeSaleBuy02()
    {
        CreditMutuelAllianceFederalePDFExtractor extractor = new CreditMutuelAllianceFederalePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "ExchangeSaleBuy02.txt"), errors);

        assertThat(errors, empty());
        assertThat(countSecurities(results), is(2L));
        assertThat(countBuySell(results), is(2L));
        assertThat(countAccountTransactions(results), is(0L));
        assertThat(results.size(), is(4));
        new AssertImportActions().check(results, "EUR");

        // check security
        assertThat(results, hasItem(security( //
                        hasIsin("FR0007076930"), hasWkn(null), hasTicker(null), //
                        hasName("CENTIFOLIA C"), //
                        hasCurrencyCode("EUR"))));

        // check security
        assertThat(results, hasItem(security( //
                        hasIsin(null), hasWkn(null), hasTicker(null), //
                        hasName("FDS EUROS SURAVENIR RENDEMENT"), //
                        hasCurrencyCode("EUR"))));

        // check buy sell transaction
        assertThat(results, hasItem(sale( //
                        hasDate("2025-02-09T00:00"), hasShares(143.0767), //
                        hasSource("ExchangeSaleBuy02.txt"), //
                        hasNote(null), //
                        hasAmount("EUR", 142.95), hasGrossValue("EUR", 143.05), //
                        hasTaxes("EUR", 0.00), hasFees("EUR", 0.10))));

        // check buy sell transaction
        assertThat(results, hasItem(purchase( //
                        hasDate("2025-02-11T00:00"), hasShares(369.52), //
                        hasSource("ExchangeSaleBuy02.txt"), //
                        hasNote(null), //
                        hasAmount("EUR", 142.95), hasGrossValue("EUR", 142.95), //
                        hasTaxes("EUR", 0.00), hasFees("EUR", 0.00))));
    }
}
