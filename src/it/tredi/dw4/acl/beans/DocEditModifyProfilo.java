package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Profilo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;

import org.dom4j.Document;

public class DocEditModifyProfilo extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Profilo profilo;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditModifyProfilo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.profilo = new Profilo();
    	this.profilo.init(domDocumento);    			
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.profilo.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("persona_interna", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//Loadingbar - aggiornamento delle persone interne
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				AclLoadingbar aclLoadingbar = new AclLoadingbar();
				aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				aclLoadingbar.init(response);
				setLoadingbar(aclLoadingbar);
				aclLoadingbar.setActive(true);
				return null;
			}			
			buildSpecificShowdocPageAndReturnNavigationRule("profilo", response);		
			return "showdoc@profilo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}

	public Profilo getProfilo() {
		return this.profilo;
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("profilo", response);
			return "showdoc@profilo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
			
	}
	
	public boolean checkRequiredField() {
		boolean result = false;
		if (profilo.getNome_profilo() == null || "".equals(profilo.getNome_profilo().trim())) {
			this.setErrorMessage("templateForm:profilo_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (profilo.getCod_amm() == null || "".equals(profilo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:profilo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (profilo.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:profilo_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (profilo.getCod_aoo() == null || "".equals(profilo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:profilo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (profilo.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:profilo_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}	

}
