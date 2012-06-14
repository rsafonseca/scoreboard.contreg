/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Jaanus Heinlaid
 */

package eionet.cr.harvest.load;


import java.io.InputStream;
import java.sql.Connection;

import org.openrdf.repository.RepositoryConnection;

/**
 * Implementation of {@link ContentLoader} for the content in RRS/Atom format.
 *
 * @author Jaanus Heinlaid
 */
public class RSSFormatLoader implements ContentLoader{

    /**
     * @see eionet.cr.harvest.load.ContentLoader#load(InputStream, org.openrdf.repository.RepositoryConnection, Connection, String, String)
     */
    @Override
    public int load(InputStream inputStream, RepositoryConnection repoConn, Connection sqlConn, String baseUri, String contextUri) {
        return 0;
    }
}
