package org.jvnet.hyperjaxb3.ejb.extensions.naming.pre_0_6_0.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;
import org.jvnet.hyperjaxb3.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.impl.DefaultNaming;
import org.springframework.beans.factory.InitializingBean;

public class CustomSimpleNoUnderscoreNaming extends DefaultNaming implements InitializingBean {
	
	@SuppressWarnings("unused")
	private boolean updated = false;
	private Map<String, String> nameKeyMap = new TreeMap<String, String>();
	private Map<String, String> keyNameMap = new TreeMap<String, String>();

	public void afterPropertiesSet() throws Exception {
		final Set<Entry<Object, Object>> entries = getReservedNames()
				.entrySet();
		for (final Entry<Object, Object> entry : entries) {
			final Object entryKey = entry.getKey();
			if (entryKey != null) {
				final String key = entryKey.toString().toUpperCase();
				final Object entryValue = entry.getValue();
				final String value = entryValue == null
						|| "".equals(entryValue.toString().trim()) ? key + "_"
						: entryValue.toString();
				nameKeyMap.put(key, value);
				keyNameMap.put(value, key);
			}
		}
	}

	public String getName(Mapping context, final String draftName) {
		Validate.notNull(draftName, "Name must not be null.");
		String intermediateName = draftName.replace('$', '_');
		final String name = intermediateName.toUpperCase();
		if (nameKeyMap.containsKey(name)) {
			return (String) nameKeyMap.get(name);
		} else if (name.length() >= getMaxIdentifierLength()) {
			for (int i = 0;; i++) {
				final String suffix = Integer.toString(i);
				final String prefix = name.substring(0,
						getMaxIdentifierLength() - suffix.length() - 1);
				final String identifier = prefix + "_" + suffix;
				if (!keyNameMap.containsKey(identifier)) {
					nameKeyMap.put(name, identifier);
					keyNameMap.put(identifier, name);
					updated = true;
					return identifier;
				}
			}
		} else if (getReservedNames().containsKey(name.toUpperCase())) {
			return name + "_";
		} else {
			return name;
		}
	}

}
