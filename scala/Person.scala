class Person (val name: String, val age: Int) {
    
    def this(name: String) {
        this(name, 0)
        this.name = name
    }

    def this(name: String, age: Int) {
        this(name)
        this.age = age
    }
}

// vim: set ts=4 sw=4 et:
