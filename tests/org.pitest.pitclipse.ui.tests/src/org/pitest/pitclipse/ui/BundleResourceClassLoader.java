/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.ui;

import org.eclipse.core.runtime.FileLocator;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.list;
import static java.util.stream.Collectors.toList;

public class BundleResourceClassLoader extends ClassLoader {

    public BundleResourceClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public URL getResource(String name) {
        return resolve(super.getResource(name));
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> urls = super.getResources(name);

        List<URL> normalized = list(urls).stream()
                                         .map(this::resolve)
                                         .filter(Objects::nonNull)
                                         .collect(toList());

        return Collections.enumeration(normalized);
    }

    private URL resolve(URL url) {
        try {
            return FileLocator.resolve(url);
        } catch (IOException e) {
            return null;
        }
    }

}
