package pather.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

import pather.game.Pather;

/**
 * Created by Miquel on 19.3.2018.
 */

public class MapEncoder {

    private final String skeleton =     "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<map version=\"1.0\" tiledversion=\"1.1.3\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"50\" height=\"20\" tilewidth=\"32\" tileheight=\"32\" infinite=\"0\" nextobjectid=\"1\">\n" +
                                            "<tileset firstgid=\"1\" name=\"pather_tilesets_334x6400\" tilewidth=\"32\" tileheight=\"32\" tilecount=\"1008\" columns=\"14\">\n" +
                                                "<image source=\"pather_tilesets_334x6400.png\" width=\"448\" height=\"2304\"/>\n" +
                                            "</tileset>\n" +
                                            "<tileset firstgid=\"1009\" name=\"winzone_tileset\" tilewidth=\"32\" tileheight=\"32\" tilecount=\"2\" columns=\"2\">\n" +
                                                "<image source=\"winzone_tileset.png\" width=\"64\" height=\"32\"/>\n" +
                                            "</tileset>\n" +
                                            "<layer name=\"Background Layer\" width=\"50\" height=\"20\">\n" +
                                                "<data encoding=\"csv\">\n" +
                                                "</data>\n" +
                                            "</layer>\n" +
                                            "<layer name=\"Graphic Layer\" width=\"50\" height=\"20\">\n" +
                                                "<data encoding=\"csv\">\n" +
                                                "</data>\n" +
                                            "</layer>\n" +
                                            "<objectgroup name=\"Ground Layer\">\n" +
                                            "</objectgroup>\n" +
                                            "<objectgroup name=\"Object Layer\">\n" +
                                            "</objectgroup>\n" +
                                            "<objectgroup name=\"Danger Zone Layer\">\n" +
                                            "</objectgroup>\n" +
                                            "<objectgroup name=\"Powerup Layer\">\n" +
                                            "</objectgroup>\n" +
                                            "<objectgroup name=\"EnemyType1 Layer\">\n" +
                                            "</objectgroup>\n" +
                                        "</map>";

    private long[] array;
    private ArrayList<long[]> tiledata = new ArrayList<long[]>();
    private ArrayList<Document> documents = new ArrayList<Document>();
    private int width = 20;
    private int height = 20;
    private InputStream io = new ByteArrayInputStream(skeleton.getBytes());
    private Document dom;
    private Element  map, temp;
    private NodeList data, layers, objects;
    private DocumentBuilder builder;
    private DocumentBuilderFactory factory;
    private String[] str = new String[] { "", "" };
    private int mapIndex, tileIndex, layerIndex, length;
    private long tile;
    private int progress = 0, pointer = 0;
    private final String[] winZoneFG = {    "0,0,0,0,0,0,0,92,93,",
                                            "0,0,0,0,0,0,105,106,107,",
                                            "113,114,115,116,117,118,119,120,121,",
                                            "127,128,129,130,131,132,133,134,135," };
    private final String[] winZoneBG = {    "45,45,45,45,45,45,45,45,45,",
                                            "59,59,59,59,59,59,59,59,59,",
                                            "73,73,73,73,73,73,73,73,73,",
                                            "87,87,87,87,87,87,87,87,87,",
                                            "87,87,87,87,87,87,87,87,87,",
                                            "101,101,101,101,101,101,101,101,101," };

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

        if(Gdx.files.internal("maps/" + mapName + ".tmx").exists())
            file = Gdx.files.internal("maps/" + mapName + ".tmx"); //Luetaan tiedosto
        else
            file = Gdx.files.local(mapName + ".tmx");

        Pather.stages += mapName + " "; //Kenttien nimien lisääminen kenttälistaan

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

        if(progress == 0) {
            length = tiledata.size();
            if(length == 0) return; //Ei luotavia kenttiä, peruuta operaatio
            temp = (Element)documents.get(0).getElementsByTagName("map").item(0);
            setHeight(Integer.parseInt(temp.getAttribute("height")));
            setWidth(Integer.parseInt(temp.getAttribute("width"))*length);
            array = new long[width*height]; //valmistele taulukko kentälle
            //määritä kentän parametrit
        }

        for(; pointer < height*width*2; pointer++) {

            //jokaisen kentän jokaisen tason jokaisen tiilen iteroimisen logiikka
            mapIndex = (int) Math.floor(pointer/(width/length))%length;
            layerIndex = (int) Math.floor(pointer/(width*height));
            tileIndex = (int) Math.floor(pointer/width)*(width/length)+pointer%(width/length);
            tile = tiledata.get(mapIndex)[tileIndex];

            //tiili lisätään merkkijonoon
            str[layerIndex] += String.valueOf(tile)+",";

            //winzone
            if((pointer+1) % width == 0) {
                if (layerIndex == 0)
                    str[layerIndex] += winZoneBG[(int) Math.min(Math.max(Math.floor((pointer) / width)-7, 0), 5)]; //background
                else {
                    Double row = Math.floor((pointer - width*height) / width);
                    if (row < 16)
                        str[layerIndex] += "0,0,0,0,0,0,0,0,0,";
                    else
                        str[layerIndex] += winZoneFG[(int) (row - 16)];
                }
            }

            if(pointer != 0 && pointer % ((width/length)*height) == 0) { //enkoodauksen pätkiminen
                progress++;
                pointer++;
                return;
            }

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
        pointer = 0;
        progress++;

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
            index +=    Integer.parseInt((currentmap).getAttribute("nextobjectid")) - 1;
            xIndex +=   Integer.parseInt((currentmap).getAttribute("width")) *
                        Integer.parseInt((currentmap).getAttribute("tilewidth"));
        }

        //Winzone object
        String[][] vals = new String[][] {  { String.valueOf(xIndex), String.valueOf(32*(height-2)), String.valueOf(192), String.valueOf(64) },
                                            { String.valueOf(xIndex+224), String.valueOf(32*(height-4)), String.valueOf(64), String.valueOf(128) },
                                            { String.valueOf(xIndex+192), String.valueOf(32*(height-2)), String.valueOf(32), String.valueOf(64) }};
        Element winzone = dom.createElement("object");;
        for(int i = 0; i < 3; i++) {
            if (i != 0) winzone = dom.createElement("object");
            winzone.setAttribute("id", String.valueOf(index++));
            winzone.setAttribute("x", vals[i][0]);
            winzone.setAttribute("y", vals[i][1]);
            winzone.setAttribute("width", vals[i][2]);
            winzone.setAttribute("height", vals[i][3]);
            objects.item(0).appendChild(winzone);
        }
        Element property = (Element) winzone.appendChild(dom.createElement("properties")).appendChild(dom.createElement("property"));
        property.setAttribute("name", "win");


        map.setAttribute("nextobjectid", String.valueOf(index));
        array = null;

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

            FileHandle file = Gdx.files.local("generated.tmx");
            file.writeString(xmlString, false); //kenttä tallennetaan lokaaliin
        } catch (Exception e) { System.out.println("ERROR ENCODING"); return; }
    }

    public int getProgress() {
        int result = progress;
        progress = (result == length*2) ? 0 : progress;
        return result;
    }

    public void setWidth(int w) {
        width = w;
        map.setAttribute("width", String.valueOf(w));
        ((Element) layers.item(0)).setAttribute("width", String.valueOf(w+9));
        ((Element) layers.item(1)).setAttribute("width", String.valueOf(w+9));
    }

    public void setHeight(int h) {
        height = h;
        map.setAttribute("height", String.valueOf(h));
        ((Element) layers.item(0)).setAttribute("height", String.valueOf(h));
        ((Element) layers.item(1)).setAttribute("height", String.valueOf(h));
    }
}
