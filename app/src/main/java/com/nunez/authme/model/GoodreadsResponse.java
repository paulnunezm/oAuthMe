package com.nunez.authme.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A class to parse the xml response using the SimpleXmlConverter factory of Retrofit.
 *
 * Parsing reference {@url https://www.javacodegeeks.com/2011/02/android-xml-binding-simple-tutorial.html}
 */
@Root(name = "GoodreadsResponse", strict = false)
public class GoodreadsResponse {
  @Element
  public User user;

  public User getUser() {
    return user;
  }
}

