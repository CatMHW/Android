package com.radaee.excel;

import java.io.BufferedWriter;  
import java.io.ByteArrayOutputStream;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
import java.io.OutputStreamWriter;  
import java.util.List;  
 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.transform.OutputKeys;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  
 
//import org.apache.commons.io.output.ByteArrayOutputStream;  
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;  
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.PicturesManager;  
import org.apache.poi.hwpf.converter.WordToHtmlConverter;  
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.hwpf.usermodel.Picture;  
import org.apache.poi.hwpf.usermodel.PictureType;  
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;  
public class WordExcelToHtml {  
 
   public static void main(String argv[]) {  
       try {  
           convertHtml("f://textTablePicture.doc","f://1.html","f://test","test");  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
   }  
 
   public static void writeFile(String content, String path) {  
       FileOutputStream fos = null;  
       BufferedWriter bw = null;  
       try {  
           File file = new File(path);  
           fos = new FileOutputStream(file);  
           bw = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"));  
           bw.write(content);  
       } catch (FileNotFoundException fnfe) {  
           fnfe.printStackTrace();  
       } catch (IOException ioe) {  
           ioe.printStackTrace();  
       } finally {  
           try {  
               if (bw != null)  
                   bw.close();  
               if (fos != null)  
                   fos.close();  
           } catch (IOException ie) {  
           }  
       }  
   }  
 
   //doc
   public static void convertHtml(String fileName, String outPutFile,String path,final String folder) throws Exception{  
       HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(fileName));//WordToHtmlUtils.loadDoc(new FileInputStream(inputFile));  
          WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(   DocumentBuilderFactory.newInstance().newDocumentBuilder() .newDocument());  
          wordToHtmlConverter.setPicturesManager( new PicturesManager(){  
             public String savePicture( byte[] content,  PictureType pictureType, String suggestedName,  float widthInches, float heightInches ){
            	 System.out.println("suggestedName="+suggestedName);
            	 return folder+"/"+suggestedName;  
              }  
            } );  
        wordToHtmlConverter.processDocument(wordDocument);  

       //save pictures  
       List pics=wordDocument.getPicturesTable().getAllPictures();  
      
       if(pics!=null){  
            for(int i=0;i<pics.size();i++){  
                Picture pic = (Picture)pics.get(i);  
                try {  
                	System.out.println("picName="+pic.suggestFullFileName());
                    pic.writeImageContent(new FileOutputStream(path+folder+"/" + pic.suggestFullFileName()));  
                } catch (FileNotFoundException e) {  
                    e.printStackTrace();  
                }     
            }  
        }  
        Document htmlDocument = wordToHtmlConverter.getDocument();  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        DOMSource domSource = new DOMSource(htmlDocument);  
        StreamResult streamResult = new StreamResult(out);  
  
        TransformerFactory tf = TransformerFactory.newInstance();  
        Transformer serializer = tf.newTransformer();  
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");  
        serializer.setOutputProperty(OutputKeys.METHOD, "html");  
        serializer.transform(domSource, streamResult);  
        out.close();  
        writeFile(new String(out.toByteArray()), outPutFile);  
    } 
}  
