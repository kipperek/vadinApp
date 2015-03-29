package pl.vadinOne.web;

import javax.servlet.annotation.WebServlet;

import pl.vadinOne.web.Broadcaster.BroadcastListener;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@Push
public class MyVaadinUI extends UI implements BroadcastListener
{
	//bean
	private Osoba osoba = new Osoba("Anonymous");
	final BeanItem<Osoba> item = new BeanItem<Osoba> (osoba);
	
	//container do tabeli wiadomosci
	private final Container msg = new IndexedContainer();
	//tabela wiadomosci
	final Table table = new Table("Wiadomości");
	
	//text field wysylajacy wiadomosc
	final TextField msgField = new TextField("Wiadomość:");

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "pl.vadinOne.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
    
    public void sendMsg(){
    	String username = (String) item.getItemProperty("nick").getValue();                
        username = "<b style='vertical-align: top;'>" +username+ "</b>";
        String msgVal = msgField.getValue();
        if(msgVal.length() > 0){
        	 msgVal = msgVal.replace("<","&lt;");
             msgVal = msgVal.replace(">","&gt;");
             
             msgVal = msgVal.replace("xD","<img src='/VAADIN/xd.png' style='width:20px; height: 20px;vertical-align: bottom;' alt/>");
             
         	 Broadcaster.broadcast(username + ": " + msgVal);
         	 msgField.setValue("");
        }
       
    }

    @Override
    protected void init(VaadinRequest request) {
    	  Broadcaster.register(this);
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
                       
        FormLayout form = new FormLayout();

        TextField nameField = new TextField("Nick:",item.getItemProperty("nick"));
        nameField.setImmediate(true);
        form.addComponent(nameField);
        
        layout.addComponent(form);
       
        Button btn = new Button("Wyślij!");
        btn.addStyleName("msgButton");
        btn.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	sendMsg();
            }
        }); 
        
        msgField.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
        	@Override
        	public void handleAction(Object sender, Object target) {
        		sendMsg();
        	}
        });
        
        msgField.setSizeFull();
        msg.addContainerProperty("wiadomosc", Label.class, null);
        table.setContainerDataSource(msg);
        table.setSizeFull();
        layout.addComponent(table);
        
        Label twojNick = new Label(item.getItemProperty("nick"));
        twojNick.setCaption("Twój nick:");
        layout.addComponent(twojNick);
        layout.addComponent(msgField);
        layout.addComponent(btn);

    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }
    
	@Override
	public void receiveBroadcast(final String message) {
		 access(new Runnable() {
	            @Override
	            public void run() {
	                Object itemId = msg.addItem();
	                 Label usernameLabel = new Label(message);
	                 usernameLabel.setContentMode(ContentMode.HTML);
	                 msg.getItem(itemId).getItemProperty("wiadomosc").setValue(usernameLabel);
	            }
	        });
		
	}

}
