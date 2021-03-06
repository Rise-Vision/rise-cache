// Copyright � 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.risecache.cache;

import java.util.Date;

public class FileInfo {

	public String url;
	public Date lastModified;
	public String etag;
	
	public FileInfo(String url, Date lastModified, String etag) {
		this.url = url;
		this.lastModified = lastModified;
		this.etag = etag;
	}

}
