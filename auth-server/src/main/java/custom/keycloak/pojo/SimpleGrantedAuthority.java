//package custom.keycloak.pojo;
//
//import org.springframework.util.Assert;
//
//public final class SimpleGrantedAuthority implements GrantedAuthority {
//    private static final long serialVersionUID = 520L;
//    private final String authority;
//
//    public SimpleGrantedAuthority(String authority) {
//        Assert.hasText(authority, "A granted authority textual representation is required");
//        this.authority = authority;
//    }
//
//    public String getAuthority() {
//        return this.authority;
//    }
//
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        } else {
//            return obj instanceof SimpleGrantedAuthority ? this.authority.equals(((SimpleGrantedAuthority)obj).authority) : false;
//        }
//    }
//
//    public int hashCode() {
//        return this.authority.hashCode();
//    }
//
//    public String toString() {
//        return this.authority;
//    }
//}
