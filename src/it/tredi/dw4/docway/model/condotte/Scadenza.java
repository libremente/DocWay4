package it.tredi.dw4.docway.model.condotte;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Scadenza extends XmlEntity {
	
	private String text = "";

	@Override
	public XmlEntity init(Document dom) {
		this.text = XMLUtil.parseStrictElement(dom, "scadenza");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix, this.text);
    	
    	return params;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
