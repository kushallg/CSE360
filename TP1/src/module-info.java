module FoundationsF25 {
    requires javafx.controls;
    requires java.sql;
    
    exports entityClasses;
    exports guiDiscussions;

    opens applicationMain to javafx.graphics, javafx.fxml;
    opens entityClasses to javafx.base;
    

    opens passwordPopUpWindow to javafx.graphics, javafx.fxml;
}