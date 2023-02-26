package Tests;

import service.*;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    void fillData() {
        taskManager = new InMemoryTaskManager();
        super.prepareData();
    }
}