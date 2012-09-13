package com.matburt.mobileorg.Gui;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;
import com.matburt.mobileorg.R;
import com.matburt.mobileorg.OrgData.OrgNode;
import com.matburt.mobileorg.util.OrgNode2Html;
import com.matburt.mobileorg.util.OrgUtils;

public class ViewFragment extends SherlockFragment {
	
	private ContentResolver resolver;
	private WebView webView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_fragment, container);
		
		this.webView = (WebView) view.findViewById(R.id.viewfragment_webview);
		this.webView.setWebViewClient(new InternalWebViewClient());
		this.webView.getSettings().setBuiltInZoomControls(true);
		this.webView.setBackgroundColor(0x00000000);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.resolver = getActivity().getContentResolver();
	}
	
	public void displayPayload(OrgNode node) {
		
	}
	
	public void displayOrgNode(OrgNode node, int levelOfRecursion, ContentResolver resolver) {
		OrgNode2Html htmlNode = new OrgNode2Html(resolver);
		htmlNode.setupConfig(getActivity());
		String html = htmlNode.convertToHTML(node, levelOfRecursion);
		displayHtml(html);
	}

	public void displayError() {
		String html = "<html><body><font color='white'>"
				+ getString(R.string.node_view_error_loading_node)
				+ "</font></body></html>";
		displayHtml(html);
	}
	
	public void displayHtml(String html) {
		this.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}

	private class InternalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				URL urlObj = new URL(url);
				if (urlObj.getProtocol().equals("file")) {
					handleInternalOrgUrl(url);
					return true;
				}
			} catch (MalformedURLException e) {
				Log.e("MobileOrg", "Malformed url :" + url);
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch(ActivityNotFoundException e) {}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
		}

	}
	
	private void handleInternalOrgUrl(String url) {		
		long nodeId = OrgUtils.getNodeFromPath(url, resolver);
				
		Intent intent = new Intent(getActivity(), ViewActivity.class);
		intent.putExtra(ViewActivity.INTENT_NODE, nodeId);
		startActivity(intent);
	}
}