import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * @author Lupy
 * @since 2019/2/24
 * @description 编译protobuf任务组
 */
class CompileTask extends DefaultTask {


    CompileTask() {
        group = 'Protobuf'
        outputs.upToDateWhen { false }//不会执行增量构建
    }

    /**
     * 执行任务的代码
     */
    @TaskAction
    def run() {
        //创建输出目录
        println "------------------------编译protobuf开始---------------------------"

        def outDir = outputs.files.singleFile
        outDir.deleteOnExit()
        outDir.mkdirs()


        def cmd = [project.protobuf.protoc.path]
        cmd << "--java_out=$outDir"

        def sources = []
        def inputDirs = inputs.files.files
        inputDirs.each {
            cmd << "-I${it.path}"
        }
        println "=====》收集编译文件"
        listFils(inputDirs, sources)
        cmd.addAll(sources)
        println "执行命令：${cmd}"

        Process process = cmd.execute()
        def stdout = new StringBuffer()
        def stderror = new StringBuffer()
        process.waitForProcessOutput(stdout,stderror)//等待编译出结果
        if (process.exitValue() == 0) {
            println "执行成功"
        } else {
            println "执行失败,${stderror}"
        }
        println "------------------------编译protobuf结束---------------------------"
    }

    /**
     * 编译目录，将目录下面所有的.proto文件添加到source集合
     * @param dirs
     * @param sources
     */
    def listFils(dirs, sources) {
        if (dirs != null && dirs.size() != 0) {
            dirs.each { File file ->
                if (file.isDirectory()) {
                    listFils(file.listFiles(), sources)
                } else {
                    if (file.name.endsWith('.proto')) {
                        println "need compile file ==> ${file.path}"
                        sources.add(file)
                    }
                }
            }
        }
    }
}