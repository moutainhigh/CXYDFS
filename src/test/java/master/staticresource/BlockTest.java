package master.staticresource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void getBlockID() {

        System.out.println(Block.getBlockID(6001l));
    }
}