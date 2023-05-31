import com.itextpdf.text.DocumentException;
import sk.tmconsulting.evidencianakladov.models.Funkcionalita;
import sk.tmconsulting.evidencianakladov.models.Vydavok;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws SQLException, DocumentException, IOException {
        Funkcionalita funkcionalita = new Funkcionalita();
        funkcionalita.setConn(); // Pripojit sa na databazu

        // START Ulozenie vydavku
/*        Vydavok vydavokObjekt = new Vydavok();
        vydavokObjekt.setPopis("Čerešne");
        vydavokObjekt.setCena(10.9);
        vydavokObjekt.setKategoria("Ovocie");
        vydavokObjekt.setDatum(Date.valueOf(LocalDate.of( 2023 , 2 , 28 )));
        funkcionalita.ulozMySQL(vydavokObjekt);*/
        // END Ulozenie vydavku




        // START Aktualizacia vydavku
/*        Vydavok vydavokObjekt = new Vydavok();
        vydavokObjekt.setPopis("Sukňa");
        vydavokObjekt.setCena(20.9);
        vydavokObjekt.setKategoria("Oblečenie");
        vydavokObjekt.setDatum(Date.valueOf(LocalDate.of( 2023 , 5 , 31 )));
        funkcionalita.aktualizujMySQL(3, vydavokObjekt);*/
        // END Aktializacia vydavku

        //funkcionalita.odstranMySQL(2);

        // START Zobrazenie vydavkov
        ArrayList<Vydavok> vydavky = funkcionalita.vyberVsetkyMySQL();
        for (Vydavok vydavok : vydavky) {
            System.out.println(vydavok);
        }
        // END Zobrazenie vydavkov

        // Zobraz celkove vydavky + financu menu Eur
        double celkoveVydavky = funkcionalita.spocitajVsetkyVydavky();
        System.out.println("Celkové výdavky sú: " + funkcionalita.pridajFinancnuMenu(celkoveVydavky, "Eur"));
        System.out.println("Celkové výdavky v CZK po konverzii z EUR sú: " + funkcionalita.konverziaMeny(celkoveVydavky, "CZK"));
        System.out.println("Celkové výdavky v USD po konverzii z EUR sú: " + funkcionalita.konverziaMeny(celkoveVydavky, "USD"));

        ArrayList<Vydavok> vydavkyPreSucet = funkcionalita.vyberVsetkyMySQL();
        double sum = 0;
        for (Vydavok vydavok : vydavkyPreSucet) {
            sum += vydavok.getCena();
        }
        System.out.println("Celkové výdavky prostredníctvom foreach sú: " + sum);

        HashMap<String, Double> kategorieCelkoveVydavky = funkcionalita.spocitajVsetkyVydavkyPodlaKategorie();
        for (String kategoria : kategorieCelkoveVydavky.keySet()) {
            double hodnota = kategorieCelkoveVydavky.get(kategoria);
            System.out.println(kategoria + ": " + hodnota);
        }

        funkcionalita.closeConn();
    }
}
