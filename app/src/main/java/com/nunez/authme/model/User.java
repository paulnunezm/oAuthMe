package com.nunez.authme.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by paulnunez on 2/21/17.
 */
@Root(name = "user", strict = false)
public class User {

  @Attribute
  public String id;

  @Element(name = "name")
  public String name;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}