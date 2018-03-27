package pather.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Miquel on 19.3.2018.
 */

public class MapEncoder {

    private final String skeleton =     "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<map version=\"1.0\" tiledversion=\"1.1.3\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"50\" height=\"20\" tilewidth=\"32\" tileheight=\"32\" infinite=\"0\" nextobjectid=\"1\">\n" +
                                            "<tileset firstgid=\"1\" name=\"sheet1\" tilewidth=\"32\" tileheight=\"32\" tilecount=\"64\" columns=\"8\">\n" +
                                            "<image source=\"sheet1.png\" width=\"256\" height=\"256\"/>\n" +
                                            "</tileset>\n" +
                                            "<tileset firstgid=\"65\" name=\"sci-fi-platformer-tiles-32x32-extension\" tilewidth=\"32\" tileheight=\"32\" tilecount=\"1760\" columns=\"16\">\n" +
                                            "<image source=\"sci-fi-platformer-tiles-32x32-extension.png\" width=\"512\" height=\"3520\"/>\n" +
                                            "</tileset>" +
                                            "<layer name=\"Background Layer\" width=\"50\" height=\"20\">\n" +
                                                "<data encoding=\"csv\">\n" +
                                                "</data>\n" +
                                            "</layer>\n" +
                                            "<layer name=\"Graphic Layer\" width=\"50\" height=\"20\">\n" +
                                                "<data encoding=\"csv\">\n" +
                                                "</data>\n" +
                                            "</layer>" +
                                            "<objectgroup name=\"Ground Layer\">" +
                                            "</objectgroup>" +
                                            "<objectgroup name=\"Object Layer\">" +
                                            "</objectgroup>" +
                                            "<objectgroup name=\"Danger Zone Layer\">" +
                                            "</objectgroup>" +
                                            "<objectgroup name=\"Powerup Layer\">" +
                                            "</objectgroup>" +
                                            "<objectgroup name=\"EnemyType1 Layer\">" +
                                            "</objectgroup>" +
                                        "</map>";

    private long[] array;
    private ArrayList<long[]> tiledata = new ArrayList<long[]>();
    private ArrayList<Document> documents = new ArrayList<Document>();
    private int width = 20;
    private int height = 20;
    private InputStream io = new ByteArrayInputStream(skeleton.getBytes());
    private Document dom;
    private Element  map;
    private NodeList data, layers, objects;
    private DocumentBuilder builder;
    private DocumentBuilderFactory factory;

    public MapEncoder() {
        try { //rungon kääntäminen XML muotoon
            io = new ByteArrayInputStream(skeleton.getBytes());
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            dom = builder.parse(io);

            objects = dom.getElementsByTagName("objectgroup");

            data = dom.getElementsByTagName("data");
            map = (Element)dom.getElementsByTagName("map").item(0);

            layers = dom.getElementsByTagName("layer");
        } catch (Exception e) { return; }
    }

    public void clear() {
        tiledata.clear();
        documents.clear();
    }

    public void decode(String mapName) { //lue kentän tiedot levyltä ja tallenna ne taulukkoon

        FileHandle file;
        try { //etsi tiedostoa ensin internalista
            file = Gdx.files.internal(mapName + ".tmx"); //Luetaan tiedosto
        } catch (RuntimeException e) { file = Gdx.files.local(mapName + ".tmx");}

        String str = file.readString();
        Document ddom;
        Element databg;
        Element datafg;

        try {
            //Käännös XML-muotoon
            InputStream dio = new ByteArrayInputStream(str.getBytes());
            ddom = builder.parse(dio);
            NodeList datagroups = ddom.getElementsByTagName("data");
            databg = (Element)datagroups.item(0);
            datafg = (Element)datagroups.item(1);
        } catch (Exception e) { System.out.println("ERROR DECODING"); return; }

            //tasojen datan kääntäminen taulukkomuotoon ja yhdistäminen
            str = databg.getTextContent()+","+datafg.getTextContent();
            str = str.replace("\n", "").replace("\r", "");
            String[] chars = str.split(",");
            array = new long[chars.length];
            for (int i = 0; i < chars.length; i++) {
                array[i] = Long.parseLong(chars[i]);
            }

            tiledata.add(array);
            documents.add(ddom);


    }

    public void encode() { //luo kerätyistä taulukoista kenttä tiedostoon temp.tmx (local)

        //määritä kentän parametrit
        int length = tiledata.size();
        if(length == 0) return; //Ei luotavia kenttiä, peruuta operaatio

        //Taulukkoa käytetään, jos tulevaisuudessa yhdistettävien kenttien leveys vaihtelee keskenään
        /*int[] widths = new int[length];
        for(int i = 0; i < length; i++) {
            Element iterable = (Element)documents.get(0).getElementsByTagName("map").item(0);
            widths[i] = Integer.parseInt(iterable.getAttribute("width"));
        }*/

        Element temp = (Element)documents.get(0).getElementsByTagName("map").item(0);

        setHeight(Integer.parseInt(temp.getAttribute("height")));
        setWidth(Integer.parseInt(temp.getAttribute("width"))*length);

        array = new long[length*width*height]; //valmistele taulukko kentälle

        String[] str = new String[] { "", "" };
        int mapIndex, tileIndex, layerIndex;
        long tile;
        for(int i = 0; i < height*width*2; i++) {

            //jokaisen kentän jokaisen tason jokaisen tiilen iteroimisen logiikka
            mapIndex = (int) Math.floor(i/(width/length))%length;
            layerIndex = (int) Math.floor(i/(width*height));
            tileIndex = (int) Math.floor(i/width)*(width/length)+i%(width/length);
            tile = tiledata.get(mapIndex)[tileIndex];

            //tiili lisätään merkkijonoon
            str[layerIndex] += String.valueOf(tile)+",";

            //VANHENTUNUT: peliobjektien luominen tiilistä
            /*if(tile == 0) continue;
            Element e = dom.createElement("object");
            e.setAttribute("id", String.valueOf(index++));
            e.setAttribute("x", String.valueOf(i%width*32));
            e.setAttribute("y", String.valueOf(Math.floor(i/width)*32));
            Element ee = dom.createElement("polyline");
            if(tile < 3) { //määritä hitboxit
                ee.setAttribute("points", "0,0 0,32 32,32 32,0 0,0");
            } else {
                ee.setAttribute("points", "0,32 32,32 16,0 0,32");
            }
            e.appendChild(ee);
            obj.appendChild(e);*/
        }

        //Tiilien siirtäminen paikalleen
        for(int i = 0; i < 2; i++) {
            str[i] = str[i].substring(0, str[i].length()-1); //poistetaan ylimääräinen pilkku perästä
            data.item(i).setTextContent(str[i]);
        }

        int index = 0, xIndex = 0;
        for(int i = 0; i < length; i++) { //objektien lisääminen kentistä
            NodeList items = documents.get(i).getElementsByTagName("objectgroup");
            if(i > 0) index = Integer.parseInt(((Element) documents.get(i-1).getElementsByTagName("map").item(0)).getAttribute("nextobjectid"));
            for (int k = 0; k < items.getLength(); k++) { //objektiryhmät
                for(Node item = items.item(k).getFirstChild(); item != null; item = item.getNextSibling()) { //objektit
                    if(item.getNodeType() == Element.ELEMENT_NODE) {
                        //ID ja x-koordinaatit
                        Element castItem = (Element) item;
                        castItem.setAttribute("id", String.valueOf(Integer.parseInt(castItem.getAttribute("id"))+index));
                        castItem.setAttribute("x", String.valueOf(Float.parseFloat(castItem.getAttribute("x"))+xIndex));
                    }
                    objects.item(k).appendChild(dom.importNode(item, true));
                }
            }

            //tunnuksien indeksointi, X-koordinaattien laskenta
            Element currentmap = (Element) documents.get(i).getElementsByTagName("map").item(0);
            index +=    Integer.parseInt((currentmap).getAttribute("nextobjectid"));
            xIndex +=   Integer.parseInt((currentmap).getAttribute("width")) *
                        Integer.parseInt((currentmap).getAttribute("tilewidth"));
        }

        try { //tiedoston tallentamisen kannalta oleellinen jargoni
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(dom);
            trans.transform(source, result);
            String xmlString = sw.toString();
            // /jargon

            FileHandle file = Gdx.files.local("temp.tmx");
            file.writeString(xmlString, false); //kenttä tallennetaan lokaaliin
        } catch (Exception e) { System.out.println("ERROR ENCODING"); return; }
    }

    public void setWidth(int w) {
        width = w;
        map.setAttribute("width", String.valueOf(w));
        ((Element) layers.item(0)).setAttribute("width", String.valueOf(w));
        ((Element) layers.item(1)).setAttribute("width", String.valueOf(w));
    }

    public void setHeight(int h) {
        height = h;
        map.setAttribute("height", String.valueOf(h));
        ((Element) layers.item(0)).setAttribute("height", String.valueOf(h));
        ((Element) layers.item(1)).setAttribute("height", String.valueOf(h));
    }
}
