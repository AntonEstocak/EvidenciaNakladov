package sk.tmconsulting.evidencianakladov.models;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class Funkcionalita implements IFunkcionalita {
    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public void setConn() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/evidencianakladov_db"; // zmeňte URL a názov databázy podľa potreby
        String username = "root"; // zmeňte používateľské meno podľa potreby
        String password = "password"; // zmeňte heslo podľa potreby

        if (this.conn == null) {
            System.out.println("conn je ešte null");
            this.conn = DriverManager.getConnection(url, username, password);
            System.out.println("conn je už naplnený, čiže sme sa napojili na databázu");
            System.out.println("Spojenie s databázou je v poriadku!");
        }
    }

    public void closeConn() throws SQLException {
        conn.close();
        //this.conn = null; // My vynulujeme object conn prostrednictvom naplnenia hodnoty null
    }

    public Funkcionalita() throws SQLException {

    }

    @Override
    public String zadajTextovyUdaj() {
        Scanner scn = new Scanner(System.in); // inicializacia konzoly
        //String vstupnyText = scn.nextLine(); // vstup z konzoly
        //return vstupnyText;
        return scn.nextLine();
    }

    @Override
    public double zadajCiselnyUdaj() {
        Scanner scn = new Scanner(System.in); // inicializacia konzoly
        return scn.nextDouble();
    }

    @Override
    public double spocitajVsetkyVydavky() throws SQLException {
        String query = "SELECT SUM(cena) AS sucet FROM vydavky";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        System.out.println(statement);
        if(rs.next()){
            double celkovySucet = rs.getDouble("sucet");
            return celkovySucet;
        }
        return 0;
    }

    public HashMap<String, Double> spocitajVsetkyVydavkyPodlaKategorie() throws SQLException {
        HashMap<String, Double> kategorieCelkoveCeny = new HashMap<>();
        String query = "SELECT kategoria, SUM(cena) AS cena FROM vydavky GROUP BY kategoria";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            kategorieCelkoveCeny.put(rs.getString("kategoria"), rs.getDouble("cena"));
        }
        return kategorieCelkoveCeny;
    }

    public String pridajFinancnuMenu(double cislo, String akaMena) {
        return cislo + " " + akaMena;
    }

    public double konverziaMeny(double cislo, String akaMena) {
        double prepocitaneCislo;
        switch (akaMena) {
            case "CZK":
                prepocitaneCislo = cislo * 25.04;
                break;
            case "USD":
                prepocitaneCislo = cislo * 1.07;
                break;
            case "TRY":
                prepocitaneCislo = cislo * 22.13;
                break;
            default:
                prepocitaneCislo = cislo;
        }
        return prepocitaneCislo;
    }


    @Override
    public int zistiPocetVsetkychVydavkov() throws SQLException {
        String query = "SELECT COUNT(cena) AS pocet FROM vydavky";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){
            int pocet = rs.getInt("pocet");
            return pocet;
        }
        return 0;
    }

    @Override
    public void vypisMenu() {
        System.out.println("Vyber si možnosť:");
        System.out.println("(1) Zadaj novú nákladovú položku");
        System.out.println("(2) Zobraz všetky nákladové položky");
        System.out.println("(3) Spočítaj sumu nákladov");
        System.out.println("(4) Vypíš počet záznamov");
        System.out.println("(9) Koniec aplikácie");
    }

    @Override
    public void exportMySQL2PDF() throws SQLException, DocumentException, IOException {
        ArrayList<Vydavok> vydavky = vyberVsetkyMySQL(); // dynamicke pole s vydavkami naplnime metodou vyberVsetkyMySQL();
        // exportuje vsetky zaznamy s detailami a celkovym sumarom
        double celkovySumar = spocitajVsetkyVydavky(); // celkovy sumar vydavkov ziskame uz cez existuju metodu spocitajVsetkyVydavky()

        // START - generovanie PDF
        Document document = new Document(); // vytvorime prazdny PDF Dokument
        // vytvori konkretny subor HelloWorld.pdf, ktorý umiestni do priečinka \\Mac\Home\Documents\

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Report výdavkov.pdf"));

        document.open(); // dokument musime ho otvorit

        // START - vypise do PDF dokumentu text
        //Font font = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

        // pouzijeme Arial Unicode MS font, ktory podporuje diakritiku
        BaseFont bf = BaseFont.createFont("arial.ttf", BaseFont.CP1250, BaseFont.EMBEDDED);
        //BaseFont bf = BaseFont.createFont("Times New Roman", BaseFont.CP1250, BaseFont.EMBEDDED);
        Font font = new Font(bf);


        Paragraph paragraph = new Paragraph("Report výdavkov - test Čokolády, ľščťžýáíé", font);
        document.add(paragraph); // do dokumentu vpiseme text A Hello Word PDF document
        for(Vydavok vystup:vydavky) {
            document.add(   new Paragraph( "Názov vydavku: " + vystup.getPopis() + " a výška vydavku je: " + vystup.getCena() )   );
        }
        // END - vypise do PDF dokumentu text

        document.close(); // zatvorime dokument
        writer.close(); // zatvorime subor
        // END - generovanie PDF
    }

    @Override
    public void ulozMySQL(Vydavok vydavok) throws SQLException {
        String sql = "INSERT INTO vydavky (popis, cena, kategoria, datum) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, vydavok.getPopis());
        statement.setDouble(2, vydavok.getCena());
        statement.setString(3, vydavok.getKategoria());
        statement.setDate(4, vydavok.getDatum());
        statement.executeUpdate(); // uskutocnime dany statement, cize uskutocnime sql query
        System.out.println(statement);
    }

    @Override
    public ArrayList<Vydavok> vyberVsetkyMySQL() throws SQLException {
        //String sql = "SELECT * FROM vydavky";
        String sql = "SELECT id, popis, cena, kategoria, datum FROM vydavky"; // Vyberieme vsetky stlpce z tabulky vydavky. Da sa nahradit *
        PreparedStatement statement = conn.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        ArrayList<Vydavok> vydavky = new ArrayList<Vydavok>(); // vytvorili sme prazdny ArrayList, ktory bude obsahovat pouzivatelov, cize User

        while (result.next()) {
            Vydavok vydavokObjekt = new Vydavok();
            vydavokObjekt.setId(result.getInt("id"));
            vydavokObjekt.setPopis(result.getString("popis")); // nazov popis koresponduje s nazvom v databaze
            vydavokObjekt.setCena(result.getDouble("cena")); // nazov cena koresponduje s nazvom v databaze
            vydavokObjekt.setKategoria(result.getString("kategoria")); // nazov kategoria koresponduje s nazvom v databaze
            vydavokObjekt.setDatum(result.getDate("datum")); // nazov datum koresponduje s nazvom v databaze

            vydavky.add(vydavokObjekt); // naplname dynamicke pole, teda ArrayList vydavkami
        }
        return vydavky;
    }

    @Override
    public void aktualizujMySQL(int id, Vydavok vydavok) throws SQLException {
        String sql = "UPDATE vydavky SET popis = ?, cena = ?, kategoria=?, datum=? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, vydavok.getPopis());
        statement.setDouble(2, vydavok.getCena());
        statement.setString(3, vydavok.getKategoria());
        statement.setDate(4, vydavok.getDatum());
        statement.setInt(5, id);
        statement.executeUpdate(); // Realne uskutocni dany SQL prikaz, ktory je urceny pre update
        System.out.println(statement);
    }

    // TODO Aktualizacia zaznamu aj s prepisom ID. Scenar: doplnit ID, ktore sme predtym odstranili

    @Override
    public void odstranMySQL(int id) throws SQLException {
        String sql = "DELETE FROM vydavky WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate(); // Realne uskutocni dany SQL prikaz, cize v tomto pripade vymaze
    }
}
