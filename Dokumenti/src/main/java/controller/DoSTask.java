package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;

import org.apache.jasper.tagplugins.jstl.core.If;

public class DoSTask  extends TimerTask 
{

	private static HashMap<String, Set<String>> adreseSesija=new HashMap<>();
	private long min20= 1000*60*20;
	
	
	
	public DoSTask()
	{
		super();
	}

	public DoSTask(HashMap<String, Set<String>> mapa) 
	{
		adreseSesija=mapa;
	}


	@Override
	public void run() {
		for(Set<String> set: adreseSesija.values()) 
		{
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
			   String session = i.next(); 
			   if( session!=null && HttpSessionCollector.find(session)==null)
			   {
				   i.remove();
			   }
			}
		}
		
	}
	
	
}