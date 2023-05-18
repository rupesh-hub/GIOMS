import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class Test {

    public static void main(String...args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<A> typeRef
                = new TypeReference<A>() {};

        A o = mapper.readValue("{\n" +
                "  \"TestData\":\"\"\n" +
                "}", typeRef);

        System.out.println("Got " + o.getTestData());
    }
}

class A {
    @JsonProperty("TestData")
    private String testData;

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }
}
