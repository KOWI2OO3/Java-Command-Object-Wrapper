package jcow.command;

import java.util.Collection;
import java.util.List;

public interface ICommand {
    
    String invoke(String... params);

    default Collection<String> nextCompletion(String context) {
        return List.of();
    }

    default String getUsage() { return ""; } 

}
