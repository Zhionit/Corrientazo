package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum CartesianDirection {
    
    Y("y"), X("x"), NY("-y"), NX("-x");
    
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
