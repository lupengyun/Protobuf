import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile



class ProtobufPlugin implements Plugin<Project> {

    Project project;

    @Override
    void apply(Project project) {
        this.project = project
        //引入分析系统操作插件
        project.apply plugin: 'com.google.osdetector'
        def proto = project.extensions.create("protobuf", ProtobufExtention)
        proto.extensions.create("protoc", ProtobufcExtention)
        project.afterEvaluate {
            //project.apply plugin:'com.google.protobuf'
            println "protoc is ${proto.protoc.artifact}"
            //下载编译器
            if (!proto.protoc.path) {
                if (!proto.protoc.artifact) {
                    throw new GradleException("请配置编译器")
                }
                Configuration confi = project.configurations.create('haha')
                def (group, name, version) = proto.protoc.artifact.split(':')

                def notation = [group: group, name: name, version: version, classifier: project.osdetector.classifier, ext: 'exe']
                //添加一个配置到工程，如果工具在本地存在就返回，如果不存在就下载
                Dependency dependency = project.dependencies.add(confi.name, notation)
                File file = confi.fileCollection(dependency).singleFile
                if (!file.canExecute() && !file.setExecutable(true)) {
                    throw new GradleException("protoc编译器无法执行")
                }
                proto.protoc.path = file.path
            }
            //执行编译任务
            Task compileTask = project.tasks.create('compileProtobuf', CompileTask)
            compileTask.inputs.files(proto.srcDirs)
            compileTask.outputs.dir("${project.buildDir}/generated/source/probuf")
            linkProtobufToSource()
        }
    }

    /**
     * 是否是Android工程
     * @return
     */
    boolean isAndroidProject() {
        return project.plugins.hasPlugin(AppPlugin) || project.plugins.hasPlugin(LibraryPlugin)
    }

    def getAndroidVariants() {
        return project.plugins.hasPlugin(AppPlugin) ? project.android.applicationVariants : project.android.libraryVariants
    }


    def linkProtobufToSource() {
        if (androidProject) {
            println "compile in android project"
            androidVariants.each {
                BaseVariant baseVariant ->
                    baseVariant.registerJavaGeneratingTask(project.tasks.compileProtobuf,
                            project.tasks.compileProtobuf.outputs.files.files
                    )
            }
        } else {
            println "compile in java project"
            project.sourceSet.each {
                SourceSet sourceSet ->
                    def compileName = sourceSet.getCompileTaskName("java")
                    JavaCompile javaCompile = project.tasks.getByName(compileName)
                    javaCompile.dependsOn project.tasks.compileProtobuf
                    sourceSet.java.srcDirs(project.tasks.compileProtobuf.outputs.files.files)
            }
        }
    }
}