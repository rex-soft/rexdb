/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rex.db.configuration.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.rex.db.exception.DBRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XPath parser.
 *
 * @version 1.0, 2016-04-26
 * @since Rexdb-1.0
 */
public class XPathParser {

	private Document document;
	private EntityResolver entityResolver;
	private XPath xpath;
	
	private TokenParser tokenParser;

	public XPathParser(InputStream inputStream, Properties variables, EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
		this.tokenParser = new TokenParser(variables);
		this.document = createDocument(new InputSource(inputStream));
		this.xpath = XPathFactory.newInstance().newXPath();
	}

	public void setVariables(Properties variables) {
		tokenParser.setVariables(variables);
	}
	
	public void addVariables(Properties variables) {
		tokenParser.addVariables(variables);
	}

	public String evalString(String expression) {
		return evalString(document, expression);
	}

	public String evalString(XNode root, String expression) {
		return evalString(root.getNode(), expression);
	}
	
	public String evalString(Object root, String expression) {
		String result = (String) eval(expression, root, XPathConstants.STRING);
		result = tokenParser.parse(result);
		return result;
	}

	public List<XNode> evalNodes(String expression) {
		return evalNodes(document, expression);
	}

	public List<XNode> evalNodes(XNode root, String expression) {
		return evalNodes(root.getNode(), expression);
	}
	
	public List<XNode> evalNodes(Object root, String expression) {
		List<XNode> xnodes = new ArrayList<XNode>();
		NodeList nodes = (NodeList) eval(expression, root, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			xnodes.add(new XNode(this, nodes.item(i), tokenParser));
		}
		return xnodes;
	}

	public XNode evalNode(String expression) {
		return evalNode(document, expression);
	}

	public XNode evalNode(XNode root, String expression) {
		return evalNode(root.getNode(), expression);
	}
	
	public XNode evalNode(Object root, String expression) {
		Node node = (Node) eval(expression, root, XPathConstants.NODE);
		if (node == null) {
			return null;
		}
		return new XNode(this, node, tokenParser);
	}

	//-----private
	private Object eval(String expression, Object root, QName returnType) {
		try {
			return xpath.evaluate(expression, root, returnType);
		} catch (Exception e) {
			throw new DBRuntimeException("DB-F0010", e, expression, e.getMessage());
		}
	}

	private Document createDocument(InputSource inputSource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(false);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(false);
			factory.setCoalescing(false);
			factory.setExpandEntityReferences(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(entityResolver);
			builder.setErrorHandler(new ErrorHandler() {
				public void error(SAXParseException exception) throws SAXException {
					throw exception;
				}
				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
				}
				public void warning(SAXParseException exception) throws SAXException {
				}
			});
			
			return builder.parse(inputSource);
		} catch (Exception e) {
			throw new DBRuntimeException("DB-F0009", e, e.getMessage());
		}
	}

}
