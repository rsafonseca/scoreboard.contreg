package eionet.cr.web.action;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import eionet.cr.common.Resource;
import eionet.cr.search.SearchException;
import eionet.cr.search.Searcher;
import eionet.cr.web.util.display.ResourcePropertyDTO;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
@UrlBinding("/factsheet.action")
public class FactsheetActionBean extends AbstractCRActionBean{

	/** */
	private String uri;
	private Resource resource;
	private List<ResourcePropertyDTO> resourceProperties;

	/**
	 * 
	 * @return
	 * @throws SearchException
	 */
	@DefaultHandler
	public Resolution view() throws SearchException{
		resource = Searcher.getResourceByUri(uri);
		if (resource!=null)
			resourceProperties = resource.getPropertiesForFactsheet();
		return new ForwardResolution("/pages/factsheet.jsp");
	}

	/**
	 * @return the resourceUri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param resourceUri the resourceUri to set
	 */
	public void setUri(String resourceUri) {
		this.uri = resourceUri;
	}

	/**
	 * @return the resourceProperties
	 */
	public List<ResourcePropertyDTO> getResourceProperties() {
		return resourceProperties;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}
}
