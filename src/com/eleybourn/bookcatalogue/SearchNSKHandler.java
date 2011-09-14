/*
 * @copyright 2011 Ivo Ugrina
 * @license GNU General Public License
 * 
 * This file is part of Book Catalogue.
 *
 * Book Catalogue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Book Catalogue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Book Catalogue.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.eleybourn.bookcatalogue;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Bundle;
//import android.util.Log;

/* 
 * An XML handler for the NSK Croatia handler by iugrina.com
 * 
 * <?xml version="1.0"?>
 * <entry>
 * <status>1</status>
 * <author>Budd, Andy</author>
 * <title>CSS Mastery : </title>
 * <publisher>Zagreb : Dobar plan, 2007. (Zagreb : Stega tisak)</publisher>
 * <other>XXI, 257 str. : ilustr. ; 24 cm.</other>
 * </entry>
 * 
 *  
 * <?xml version="1.0"?>
 * <entry>
 * <status>0</status>
 * <errormessage>Malformed query</errormessage>
 * </entry>
 *  
 */


public class SearchNSKHandler extends DefaultHandler {
	private StringBuilder builder;
	
	private Bundle mValues;
		
	public static int mStatus = -1;
	public static String mErrorMessage;
	
	public static String STATUS = "status";
	public static String ERRORMESSAGE = "errormessage";
	public static String AUTHOR = "author";
	public static String TITLE = "title";
	public static String DATE_PUBLISHED = "date";
	public static String PUBLISHER = "publisher";
	public static String PAGES = "other";
	public static String ISBN = "isbn";
	
	SearchNSKHandler(Bundle values, int status, String errormessage) {
		mValues = values;
		mErrorMessage = errormessage;
		mStatus = status;	
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	private void addIfNotPresent(String key) {
		if (!mValues.containsKey(key) || mValues.getString(key).length() == 0) {
			mValues.putString(key, builder.toString());
		}		
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (localName.equalsIgnoreCase(STATUS)){
			mStatus = Integer.parseInt(builder.toString());
			//Log.i("BookCatalogue", "mStatus = " + builder.toString() );
		}
		if (mStatus == 0 && localName.equalsIgnoreCase(ERRORMESSAGE)){
			mErrorMessage = builder.toString();
			//Log.i("BookCatalogue", "mErrorMessage = " + builder.toString());
		}
		if( mStatus == 1 ){
			//Log.i("BookCatalogue", "mStatus == 1" );
			if (localName.equalsIgnoreCase(TITLE)){
				//Log.i("BookCatalogue", "title = " + builder.toString());
				addIfNotPresent(CatalogueDBAdapter.KEY_TITLE);
			} else if (localName.equalsIgnoreCase(ISBN)){
				String tmp = builder.toString(); 
				if (!mValues.containsKey(CatalogueDBAdapter.KEY_ISBN) || tmp.length() > mValues.getString(CatalogueDBAdapter.KEY_ISBN).length()) {
					mValues.putString(CatalogueDBAdapter.KEY_ISBN, tmp);
				}			
			} else if (localName.equalsIgnoreCase(AUTHOR)){
				Utils.appendOrAdd(mValues, CatalogueDBAdapter.KEY_AUTHOR_DETAILS, builder.toString());
			} else if (localName.equalsIgnoreCase(PUBLISHER)){
				addIfNotPresent(CatalogueDBAdapter.KEY_PUBLISHER);
			} else if (localName.equalsIgnoreCase(DATE_PUBLISHED)){
				addIfNotPresent(CatalogueDBAdapter.KEY_DATE_PUBLISHED);
			} 
			/*else if (localName.equalsIgnoreCase(PAGES)){
				String tmp = builder.toString();
				int index = tmp.indexOf(" pages");
				if (index > -1) {
					tmp = tmp.substring(0, index).trim(); 
					mValues.putString(CatalogueDBAdapter.KEY_PAGES, tmp);
				}*/
		}
		builder.setLength(0);
		//Log.i("BookCatalogue", "endElement > " + uri + "::" + localName + "::" + name);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		builder.setLength(0);
		//Log.i("BookCatalogue", "startElement > " + uri + "::" + localName + "::" + name);
	}
}
