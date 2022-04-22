package edu.rpi.legup.save;

import edu.rpi.legup.save.ExportFileException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

import edu.rpi.legup.model.Puzzle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ShortTruthTableCreator {
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
    public ShortTruthTableCreator(String[] premises) throws ExportFileException, InvalidFileFormatException{
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

            for(int i = 0; i < premises.length; i++){
                if(validGrammar(premises[i])){
                    Element statement = document.createElement("statement");
                    data.appendChild(statement);
                    statement.setAttribute("representation", premises[i]);
                    statement.setAttribute("row_index", Integer.toString(i));
                }else{
                    throw new InvalidFileFormatException("Premises #" + (i + 1) + " is malformed.");
                }
            } 

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

    //validGrammar checker taken from ShortTruthTableImporter
    private boolean validGrammar(String sentence) {
        int open = 0;
        int close = 0;
        char[] valid_characters = new char[] {'^', 'v', '!', '>', '-', '&', '|', '~', '$', '%'};
        for(int i = 0; i < sentence.length(); i++) {
            char s = sentence.charAt(i);
            if(s == '(' || s == ')'){
                switch (s) {
                    case ')':
                        close++;
                        break;
                    case '(':
                        open++;
                        break;
                }
                continue;
            }
            if(!Character.isLetter(s)) {
                boolean valid = false;
                for(char c: valid_characters) {
                    if(c == s) {
                        valid = true;
                        break;
                    }
                }
                if(!valid) {
                    System.out.println("Invalid character");
                    System.out.println(s);
                    return false;
                }
                if(i != sentence.length() - 1) {
                    char next = sentence.charAt(i+1);
                    if(next != '!' && next != '~') {
                        for(char c: valid_characters) {
                            if(c == next) {
                                System.out.println("Invalid next character");
                                System.out.println(s);
                                System.out.println(next);
                                return false;
                            }
                        }
                    }
                }
            } else {
                if(i != sentence.length() - 1) {
                    if(Character.isLetter(sentence.charAt(i+1))) {
                        System.out.println("Invalid next character");
                        System.out.println(s);
                        System.out.println(sentence.charAt(i+1));
                        return false;
                    } 
                }
            }
        }
        return open == close;
    } 
}