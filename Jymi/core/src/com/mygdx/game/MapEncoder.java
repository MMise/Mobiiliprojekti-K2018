package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
                                        "<map version=\"1.0\" tiledversion=\"1.1.3\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"20\" height=\"20\" tilewidth=\"32\" tileheight=\"32\" infinite=\"0\" nextobjectid=\"400\">\n" +
                                            "<tileset firstgid=\"1\" source=\"tilesetti.tsx\"/>\n" +
                                            "<layer name=\"Tile Layer 1\" width=\"20\" height=\"20\">\n" +
                                                "<data encoding=\"csv\">" +
                                                "</data>\n" +
                                            "</layer>\n" +
                                            "<objectgroup name=\"Object_layer\">\n" +
                                            "</objectgroup>\n" +
                                        "</map>";

    private int[] array;
    private ArrayList<int[]> maps = new ArrayList<int[]>();
    private int index;
    private int width = 20;
    private int height = 20;
    private InputStream io = new ByteArrayInputStream(skeleton.getBytes());
    private Document dom;
    private Element map, layer, data, obj;
    private DocumentBuilder builder;
    private DocumentBuilderFactory factory;

    MapEncoder() {
        try { //rungon kääntäminen XML muotoon
            io = new ByteArrayInputStream(skeleton.getBytes());
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            dom = builder.parse(io);

            obj = (Element)dom.getElementsByTagName("objectgroup").item(0);
            data = (Element)dom.getElementsByTagName("data").item(0);
            map = (Element)dom.getElementsByTagName("map").item(0);
            layer = (Element)dom.getElementsByTagName("layer").item(0);

            //taulukkoa käytetään kentän luomisen yhteydessä
            /*array = new int[]{  2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
                                2,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,
                                2,0,0,0,1,1,1,1,0,0,0,2,0,0,0,0,1,1,1,1,
                                2,1,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,0,0,2,
                                2,2,0,0,0,0,2,0,0,0,0,2,0,0,0,1,0,0,0,2,
                                2,0,0,0,0,3,2,0,0,0,0,2,0,0,0,3,3,3,3,2,
                                2,0,0,0,1,1,2,0,0,0,0,2,0,0,0,1,1,1,1,2,
                                2,0,0,0,0,3,2,0,0,0,0,2,1,0,0,0,0,0,0,2,
                                2,0,0,0,1,1,2,0,0,0,0,2,0,0,0,0,0,0,0,2,
                                2,0,0,0,0,3,2,0,0,0,0,2,0,3,0,0,0,0,0,2,
                                2,0,0,0,1,1,2,0,0,0,0,2,0,1,0,1,0,0,0,2,
                                2,0,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,1,0,2,
                                2,1,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,2,0,2,
                                2,0,0,0,0,0,2,0,0,0,0,2,0,0,0,0,3,2,0,2,
                                2,3,0,0,0,0,2,0,0,0,0,2,0,0,0,0,1,2,0,2,
                                2,1,1,0,0,0,2,0,0,0,0,2,0,0,0,0,0,0,0,2,
                                2,0,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,0,0,2,
                                2,0,0,0,1,3,2,0,0,0,0,2,0,0,0,0,0,0,0,2,
                                2,0,0,1,2,1,2,0,0,0,0,0,0,0,0,0,0,0,0,2,
                                2,1,1,2,2,2,2,0,0,0,0,1,1,0,0,1,1,3,0,2 };
            maps.add(array);*/
        } catch (Exception e) { return; }
    }

    public void clear() {
        maps.clear();
    }

    public void decode(String mapName) { //lue kentän tiedot levyltä ja tallenna ne taulukkoon

        FileHandle file;
        try { //etsi tiedostoa ensin internalista
            file = Gdx.files.internal(mapName + ".tmx"); //Luetaan tiedosto
        } catch (RuntimeException e) { file = Gdx.files.local(mapName + ".tmx");}

        try {
            String str = file.readString();

            InputStream dio = new ByteArrayInputStream(str.getBytes());
            Document ddom = builder.parse(dio);
            Element data = (Element)ddom.getElementsByTagName("data").item(0);

            str = data.getTextContent();
            str = str.replace("\n", "").replace("\r", "");
            String[] chars = str.split(",");
            array = new int[chars.length];
            for (int i = 0; i < chars.length; i++) {
                array[i] = Integer.parseInt(chars[i]);
            }
            maps.add(array);

        } catch (Exception e) { System.out.println("ERROR DECODING"); return; }
    }

    public void encode() { //luo kerätyistä taulukoista kenttä tiedostoon temp.tmx (local)

        //määritä kentän parametrit
        int length = maps.size();
        if(length == 0) return; //Ei luotavia kenttiä, peruuta operaatio
        index = 0;
        setHeight((int)Math.sqrt(maps.get(0).length));
        setWidth(height*length);

        array = new int[length*width*height]; //valmistele taulukko kentälle

        String str = "";
        int mapIndex, tileIndex, tile;
        for(int i = 0; i < height*width; i++) {

            mapIndex = (int) Math.floor(i/height)%length;
            tileIndex = (int) Math.floor(i/width)*height+i%height;
            tile = maps.get(mapIndex)[tileIndex];

            str += String.valueOf(tile)+",";
            if(tile == 0) continue;
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
            obj.appendChild(e);
        }
        map.setAttribute("nextobjectid", String.valueOf(index));
        str = str.substring(0, str.length()-1);


        data.setTextContent(str); //siirretään tiilet paikalleen

        try { //tiedoston tallentamisen kannalta oleellinen jargoni
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
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
        layer.setAttribute("width", String.valueOf(w));
    }

    public void setHeight(int h) {
        height = h;
        map.setAttribute("height", String.valueOf(h));
        layer.setAttribute("height", String.valueOf(h));
    }
}
