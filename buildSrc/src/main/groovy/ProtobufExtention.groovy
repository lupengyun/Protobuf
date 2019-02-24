
class ProtobufExtention {

    def srcDirs

    ProtobufExtention() {
        srcDirs = []
        srcDirs.add('src/main/protobuf')//添加一个默认的位置
    }


    void setDir(String dir) {
        if (!srcDirs.contains(dir)) {
            srcDirs.add(dir)
        }
    }

    void setDirs(String... dirs) {
        dirs.each {
            setDir(it)
        }
    }
}