package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ThesauroFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class AclThesauroFormsAdapter extends ThesauroFormsAdapter {

	public AclThesauroFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}
}
