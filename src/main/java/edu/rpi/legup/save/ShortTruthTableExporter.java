package edu.rpi.legup.save;

import edu.rpi.legup.save.ExportFileException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class ShortTruthTableExporter {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dbuilder;
    Document document;
    Element legup;
    Element puzzle;
    Element board;
    Element data;

    /*
     * Constructor creates the XML Representation of the problem
    */
    public ShortTruthTableExporter() throws ExportFileException{
        try {
        dbFactory = DocumentBuilderFactory.newInstance();
        dbuilder = dbFactory.newDocumentBuilder();
        document = dbuilder.newDocument();
        legup = document.createElement("Legup");
        puzzle = document.createElement("puzzle");
        board = document.createElement("board");
        data = document.createElement("data");
        //Base Short Truth Table Elements
        document.appendChild(legup); 
        legup.setAttribute("version", "3.0.0");
        legup.appendChild(puzzle); 
        puzzle.setAttribute("name", "ShortTruthTable");
        puzzle.appendChild(board);
        board.appendChild(data);
        data.setAttribute("normal", "true");
        } catch (ParserConfigurationException e) {
            throw new ExportFileException("Puzzle Exporter: parser configuration exception");
        } catch (Exception e) {
            throw e;
            //throw new ExportFileException(e.getMessage());
        }
    }

    /**
     * Exports the truth table to an xml formatted file
     *
     * @param fileName name of file to be exported
     * @throws ExportFileException
     */
    public void exportTruthTable(String fileName) throws ExportFileException {
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(fileName));

            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new ExportFileException("Puzzle Exporter: parser configuration exception");
        } catch (Exception e) {
            throw e;
            //throw new ExportFileException(e.getMessage());
        }
    }
}