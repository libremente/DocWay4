package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.RifintLookup;
import it.tredi.dw4.docway.adapters.DocWayRifintLookupFormsAdapter;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;

import org.dom4j.Document;

public class DocWayRifintLookup extends RifintLookup {
	private DocWayRifintLookupFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;
	
	private String xml;
	
	private boolean lookupSuRepertorio = false; // true se si tratta di un lookup su repertorio, false altrimenti
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocWayRifintLookup() throws Exception {
		this.formsAdapter = new DocWayRifintLookupFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {
    	xml = domTitoli.asXML();
    	this.titoli = (ArrayList<Titolo>) XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
    }	
	
	public DocWayRifintLookupFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}
	
	public boolean isLookupSuRepertorio() {
		return lookupSuRepertorio;
	}

	public void setLookupSuRepertorio(boolean lookupSuRepertorio) {
		this.lookupSuRepertorio = lookupSuRepertorio;
	}
	
	/**
	 * data l'extraquery del lookup identifica se si tratta di un lookup su repertorio o meno
	 * @param xq
	 */
	public void setLookupSuRepertorioByExtraQuery(String xq) {
		if (xq != null && xq.length() > 0) {
			if (xq.contains("[/persona_interna/personal_rights/right/@cod/]")) // xpath di verifica del permesso sul repertorio da parte dell'utente
				this.lookupSuRepertorio = true;
		}
	}
	
}
