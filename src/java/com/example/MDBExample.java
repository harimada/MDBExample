/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import static javax.ws.rs.client.Entity.xml;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import xsd.generated.ExpenseT;
import xsd.generated.ItemListT;
import xsd.generated.ItemT;
import xsd.generated.ObjectFactory;
import xsd.generated.UserT;

/**
 *
 * @author Harikrishna
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/myQueue")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MDBExample implements MessageListener {
    
    public MDBExample() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        try{
            TextMessage tm =  (TextMessage) message;
            System.out.println("text message-------------------> \n"+tm.getText());
            ExpenseT expns =unmarshal(tm.getText());
            System.out.println("--------------------------------- "+expns.getUser().getUserName());
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static ExpenseT unmarshal(String xml) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

    //2. Use JAXBContext instance to create the Unmarshaller.
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    //3. Use the Unmarshaller to unmarshal the XML document to get an instance of JAXBElement.
    JAXBElement unmarshalledObject = 
        (JAXBElement)unmarshaller.unmarshal(new InputSource(new StringReader(xml)));

    //4. Get the instance of the required JAXB Root Class from the JAXBElement.
    if(unmarshalledObject.getValue() instanceof ExpenseT){
        System.out.println("yes it is...................");
    }
    ExpenseT expenseObj = (ExpenseT)unmarshalledObject.getValue();
    UserT user = expenseObj.getUser();
    ItemListT items = expenseObj.getItems();

    //Obtaining all the required data from the JAXB Root class instance.
    System.out.println("Printing the Expense for: "+user.getUserName());
    for ( ItemT item : items.getItem()){
      System.out.println("Name: "+item.getItemName());
      System.out.println("Value: "+item.getAmount());
      System.out.println("Date of Purchase: "+item.getPurchasedOn());
    }   
    
    return expenseObj;
  }
    
    public static void main(String args[]) throws JAXBException, SAXException, ParserConfigurationException, IOException{
        
        String xyz = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
"<expenseReport>\n" +
"    <user>\n" +
"        <userName>Sanaulla</userName>\n" +
"    </user>\n" +
"    <items>\n" +
"        <item>\n" +
"            <itemName>Seagate External HDD</itemName>\n" +
"            <purchasedOn>August 24, 2010</purchasedOn>\n" +
"            <amount>6776.5</amount>\n" +
"        </item>\n" +
"    </items>\n" +
"</expenseReport>";
        
         //1. We need to create JAXContext instance
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

    //2. Use JAXBContext instance to create the Unmarshaller.
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    //3. Use the Unmarshaller to unmarshal the XML document to get an instance of JAXBElement.
    JAXBElement<ExpenseT> unmarshalledObject = 
        (JAXBElement<ExpenseT>)unmarshaller.unmarshal(new InputSource(new StringReader(xyz)));

    //4. Get the instance of the required JAXB Root Class from the JAXBElement.
    ExpenseT expenseObj = unmarshalledObject.getValue();
    UserT user = expenseObj.getUser();
    ItemListT items = expenseObj.getItems();

    //Obtaining all the required data from the JAXB Root class instance.
    System.out.println("Printing the Expense for: "+user.getUserName());
    for ( ItemT item : items.getItem()){
      System.out.println("Name: "+item.getItemName());
      System.out.println("Value: "+item.getAmount());
      System.out.println("Date of Purchase: "+item.getPurchasedOn());
    }   
    
    }
}
