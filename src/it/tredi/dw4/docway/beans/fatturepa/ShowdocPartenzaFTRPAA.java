package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.ReloadMsg;
import it.tredi.dw4.docway.beans.ShowdocPartenza;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.docway.model.fatturepa.DatiFattura;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.docway.model.fatturepa.Notifica;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.net.URLEncoder;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class ShowdocPartenzaFTRPAA extends ShowdocPartenza {

	private final String NOTIFICA_TRASMISSIONE_IMPOSSIBILE = "AT";
	private final String NOTIFICA_DECORRENZA_TERMINI = "DT";
	private final String NOTIFICA_DI_SCARTO = "NS";
	
	// campi specifici del repertorio di fatturePA passive
	private FatturaPA fatturaPA = new FatturaPA();
	
	private String fileidFattura = "";
	private String xslVisualizzazioneFattura = ""; // eventuale XSLT da applicare all'XML della fattura (specificato da file di properties)
	
	private String statoFattura = ""; // identifica lo stato della fattura (es. '', 'DT', 'SEND', 'AT', ecc.)
	
	public ShowdocPartenzaFTRPAA() throws Exception {
		super();
	}
	
	public FatturaPA getFatturaPA() {
		return fatturaPA;
	}

	public void setFatturaPA(FatturaPA fatturaPA) {
		this.fatturaPA = fatturaPA;
	}

	public String getFileidFattura() {
		return fileidFattura;
	}

	public void setFileidFattura(String fileidFattura) {
		this.fileidFattura = fileidFattura;
	}

	public String getXslVisualizzazioneFattura() {
		return xslVisualizzazioneFattura;
	}

	public void setXslVisualizzazioneFattura(String xslVisualizzazioneFattura) {
		this.xslVisualizzazioneFattura = xslVisualizzazioneFattura;
	}
	
	public String getStatoFattura() {
		return statoFattura;
	}

	public void setStatoFattura(String statoFattura) {
		this.statoFattura = statoFattura;
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		fatturaPA.init(XMLUtil.createDocument(dom, "/response/doc/extra/fatturaPA"));
		
		// recupero dell'xslt di visualizzazione
		xslVisualizzazioneFattura = FatturePaUtilis.getXsltFileForPreview(fatturaPA.getVersione());
		
		// richiesta al service del download del file XML della fattura (in caso di file P7M viene richiesto
		// il contenuto del file firmato (XML)
		fileidFattura = "";
		if (fatturaPA.getFileNameFattura() != null && !fatturaPA.getFileNameFattura().equals("") && getDoc().getFiles() != null && getDoc().getFiles().size() > 0) {
			int i = 0;
			while (fileidFattura.equals("") && i < getDoc().getFiles().size()) {
				XwFile xwFile = (XwFile) getDoc().getFiles().get(i);
				if (xwFile != null && xwFile.getTitle() != null && xwFile.getTitle().equals(fatturaPA.getFileNameFattura() + "." + fatturaPA.getExtensionFattura()))
					fileidFattura = xwFile.getName();
				i++;
			}
		}
		
		// recupero dello stato della fattura (stato corrente del documento)
		String stato = "";
		if (fatturaPA.getNotifica().size() > 0) {
			for (int i=0; i<fatturaPA.getNotifica().size(); i++) {
				Notifica notifica = fatturaPA.getNotifica().get(i);
				if (notifica.getTipo().toUpperCase().equals(NOTIFICA_DECORRENZA_TERMINI)) {
					// decorrenza termini
					stato = NOTIFICA_DECORRENZA_TERMINI;
				}
				else if (notifica.getTipo().toUpperCase().equals(NOTIFICA_TRASMISSIONE_IMPOSSIBILE)) {
					// avvenuta trasmissione della fattura con impossibilita' di recapito
					stato = NOTIFICA_TRASMISSIONE_IMPOSSIBILE;
				}
				else if (notifica.getTipo().toUpperCase().equals(NOTIFICA_DI_SCARTO)) {
					// notifica scartata dal SdI, non e' piu' possibile inviare il file con lo stesso nome
					stato = NOTIFICA_DI_SCARTO;
				}
			}
						
			// se lo stato non e' ancora stato individuato verifico se e' stato inviato un esito committente
			// separato per ogni fattura presente nel documento (notifiche su singole fatture di un lotto)
			if (stato.equals(""))
				stato = fatturaPA.getState();
		}
		statoFattura = stato;
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/fatturepa/showdoc@partenza@FTRPAA");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	/**
	 * mostra/nasconde la sezione relativi ai dettagli della fattura
	 * @return
	 * @throws Exception
	 */
	public String mostraNascondiDettagliFattura(String indexFattura) throws Exception {
		DatiFattura fattura = getFatturaByIndex(indexFattura);
		if (fattura != null)
			fattura.setShowDettagli(!fattura.isShowDettagli());
		return null;
	}
	
	/**
	 * mostra/nasconde la sezione relativi ai riferimenti della fattura
	 * @return
	 * @throws Exception
	 */
	public String mostraNascondiRiferimentiFattura(String indexFattura) throws Exception {
		DatiFattura fattura = getFatturaByIndex(indexFattura);
		if (fattura != null)
			fattura.setShowRiferimenti(!fattura.isShowRiferimenti());
		return null;
	}
	
	/**
	 * dato l'indice della lista restituisce la relativa fattura
	 * @param indexFattura
	 * @return
	 */
	private DatiFattura getFatturaByIndex(String indexFattura) {
		DatiFattura fattura = null;
		try {
			if (indexFattura != null && StringUtil.isNumber(indexFattura) && getFatturaPA() != null && getFatturaPA().getDatiFattura() != null) {
				int index = new Integer(indexFattura).intValue();
				if (index >= 0 && index < getFatturaPA().getDatiFattura().size()) 
					fattura = getFatturaPA().getDatiFattura().get(index);
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return fattura;
	}
	
	/**
	 * costruzione dell'XML della fattura secondo le specifiche per le PA
	 * @return
	 * @throws Exception
	 */
	public String generaXmlFatturaPA() throws Exception {
		try {
			// nome file fattura (TODO andrebbe recuperato da ACL)
			// TODO come gestire il progressivo univio
			String progressivoUnivoco = "00001";
			String filenameFattura = DocWayProperties.readProperty("fatturepa.committente.sede.nazione", "") + DocWayProperties.readProperty("fatturepa.committente.piva", "") + "_" + progressivoUnivoco + ".xml";
			
			formsAdapter.generaXmlFatturaPA(filenameFattura);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// lettura del messaggio di ritorno
			ReloadMsg message = new ReloadMsg();
			message.setActive(true);
			message.init(response.getDocument());
			message.setLevel(Const.MSG_LEVEL_SUCCESS);
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			reload(); // reload del documento
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("reloadmsg", message);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * invio della fatturaPA attiva tramite il sistema di interscambio
	 * @return
	 * @throws Exception
	 */
	public String inviaFatturaPA() throws Exception {
		try {
			if (fileidFattura != null && fileidFattura.length() > 0) {
				formsAdapter.inviaFatturaPAattiva(fatturaPA.getFileNameFattura() + "." + fatturaPA.getExtensionFattura());
				
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				// lettura del messaggio di ritorno
				ReloadMsg message = new ReloadMsg();
				message.setActive(true);
				message.init(response.getDocument());
				message.setLevel(Const.MSG_LEVEL_SUCCESS);
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				reload(); // reload del documento
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("reloadmsg", message);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
     * ritorna l'id del file di fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedFileidFattura() {
		String encodedId = fileidFattura;
    	try {
    		encodedId = URLEncoder.encode(encodedId, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedId;
	}
	
	/**
     * ritorna il nome del file di fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedFileNameFattura() {
		String encodedTitle = fatturaPA.getFileNameFattura() + "." + fatturaPA.getExtensionFattura();
    	try {
    		encodedTitle = URLEncoder.encode(encodedTitle, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedTitle;
	}
	
	/**
     * ritorna il nome file xsl di preview della fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedXsltFileName() {
		String encodedXslt = xslVisualizzazioneFattura;
    	try {
    		encodedXslt = URLEncoder.encode(encodedXslt, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedXslt;
	}

}
