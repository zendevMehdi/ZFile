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

## Installation

You can get jar from release section or create new project and add src folder to the project.


## Usage/Examples

Here we access all the partitions mounted on the system and fetch information from them.

```java
package org.zendev.lib.system;

import org.zendev.lib.system.partition.PartitionManager;

public class App {
    public static void main(String[] args) {
        System.out.println("Access system partitions");
        System.out.println("----------------------------------");

        PartitionManager.getPartitions(true).forEach(p -> {
            System.out.printf("%s\n", p.getPartition());
            System.out.printf("Total space: %d (bytes)\n", p.getTotalSpace());
            System.out.printf("Free space: %d (bytes)\n", p.getFreeSpace());
            System.out.printf("Used space: %d (bytes)\n", p.getUsedSpace());
            System.out.printf("System partition: %b\n", p.isSystemPartition());
        });
    }
}
```
