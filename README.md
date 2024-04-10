# ZFile

This library help developers to access files and folders information such as (modify date, creation time, owner, size, ...), it also lets you perform operations on them like (copy, rename, delete, search, ...).

## Authors

- [@Mehdi Lavasani](https://github.com/zendevMehdi)


## Features

- Access modify/create/access date and time
- Get owner
- Get size
- Check for file/folder existance
- Search a file or folder
- Rename/delete/copy items
- Get files type without need extension
- Get items name/base name and extensions
- Access items parent name or path

## Installation

You can get jar from release section or create new project and add src folder to the project.


## Usage/Examples

Here we access all the partitions mounted on the system and fetch information from them.

```java
package org.zendev.lib.file;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        var cf = new ZFile("build.txt");

        System.out.printf("Type: %s\n", cf.getType());
        System.out.printf("Name: %s\n", cf.getName());
        System.out.printf("Base name: %s\n", cf.getBaseName());
        System.out.printf("Owner: %s\n", cf.getOwner(true));
        System.out.printf("Parent path: %s\n", cf.getParentPath());
        System.out.printf("Parent name: %s\n", cf.getParentName());
        System.out.printf("Size: %s (bytes)\n", cf.getSize());
    }
}
```

# Output

Type: text/plain </br>
Name: build.txt </br>
Base name: build </br>
Owner: ZENDEV\mehdi </br>
Parent path: D:\programming\libs\ZFile </br>
Parent name: ZFile </br>
Size: 25 (bytes) </br>

Process finished with exit code 0 </br>
