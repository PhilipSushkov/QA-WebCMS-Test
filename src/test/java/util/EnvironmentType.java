package util;

public enum EnvironmentType {

  DEVELOP("aestest.s1.q4web.newtest/admin/"),
  //BETA("chicagotest.s1.q4web.release/admin/"), //chicagotest url
  BETA("chicagotest.s1.q4web.newtest/admin/"), //chicagotest url
  //BETA("test1.s1.q4preview.com/admin/"),
  //BETA("maxwell2016ir.s1.q4web.release/"),
  //BETA("facebook.s1.q4web.newtest/admin/"),
  //BETA("chicagotest.s3.q4web.com/admin/"),
  //BETA("cornerstone.s1.q4web.newtest/admin/"),

  //BETA("goldcorptest.s1.q4web.newtest/admin/"), //goldcorp url
  //BETA("kinross.s1.q4web.newtest/admin/"), //kinross url
  //BETA("intactfinancial.s1.q4web.newtest/admin/"), //intact financial url
  PRODUCTION("chicagotest.s3.q4web.com/admin/");

  private final String host;
  private final String protocol = "https://";

  EnvironmentType(String host) {

    this.host = host;
  }

  public String getHost() {

    return host;
  }

  public String getProtocol() {

    return protocol;
  }
}

