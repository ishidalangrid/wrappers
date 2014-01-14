package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.eclipse;

import jp.go.nict.langrid.commons.ws.LocalServiceContext;

public class EclipseServiceContext extends LocalServiceContext{
	@Override
	public String getRealPath(String path) {
		return "WebContent/" + path;
	}
}
