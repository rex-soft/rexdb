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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML node.
 *
 * @version 1.0, 2016-04-26
 * @since Rexdb-1.0
 */
public class XNode {

	private Node node;
	private String name;
	private String body;
	private Properties attributes;
	private XPathParser xpathParser;
	private TokenParser tokenParser;

	public XNode(XPathParser xpathParser, Node node, TokenParser tokenParser) {
		this.xpathParser = xpathParser;
		this.tokenParser = tokenParser;
		
		this.node = node;
		this.name = node.getNodeName();
		this.attributes = parseAttributes(node);
		this.body = parseBody(node);
	}

	//---node
	public Node getNode() {
		return node;
	}

	public String getName() {
		return name;
	}

	//----attribute
	public String getAttribute(String name) {
		return getAttribute(name, null);
	}

	public String getAttribute(String name, String def) {
		String value = attributes.getProperty(name);
		if (value == null) {
			return def;
		} else {
			return value;
		}
	}

	//----body
	public String getBody() {
		return getBody(null);
	}

	public String getBody(String def) {
		if (body == null) {
			return def;
		} else {
			return body;
		}
	}

	//----children
	public List<XNode> getChildren() {
		List<XNode> children = new ArrayList<XNode>();
		NodeList nodeList = node.getChildNodes();
		if (nodeList != null) {
			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					children.add(new XNode(xpathParser, node, tokenParser));
				}
			}
		}
		return children;
	}

	public Properties getChildrenAsProperties() {
		Properties properties = new Properties();
		for (XNode child : getChildren()) {
			String name = child.getAttribute("name");
			String value = child.getAttribute("value");
			if (name != null && value != null) {
				properties.setProperty(name, value);
			}
		}
		return properties;
	}

	//-----------private methods
	private Properties parseAttributes(Node n) {
		Properties attributes = new Properties();
		NamedNodeMap attributeNodes = n.getAttributes();
		if (attributeNodes != null) {
			for (int i = 0; i < attributeNodes.getLength(); i++) {
				Node attribute = attributeNodes.item(i);
				String value = tokenParser.parse(attribute.getNodeValue());
				attributes.put(attribute.getNodeName(), value);
			}
		}
		return attributes;
	}

	private String parseBody(Node node) {
		String data = getBodyData(node);
		if (data == null) {
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				data = getBodyData(child);
				if (data != null) {
					break;
				}
			}
		}
		return data;
	}

	private String getBodyData(Node child) {
		if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
			String data = ((CharacterData) child).getData();
			data = tokenParser.parse(data);
			return data;
		}
		return null;
	}

}