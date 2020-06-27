/* uses sbt, which i installed with homebrew. */
/* this works without requiring the 'sbt plugin'. */

pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                ansiColor('xterm') {
                sh "/usr/local/bin/sbt compile"
            }
            }
        }

        stage('Lint') {
            steps {
            ansiColor('xterm') {
                echo "Testing..."
                sh "/usr/local/bin/sbt scalastyle"
            }}
        }

        stage('Test') {
            steps {ansiColor('xterm') {
                echo "Testing..."
                sh "/usr/local/bin/sbt test"
                }
            }
        }


        stage('Package') {
            steps {
            ansiColor('xterm') {
                echo "Packaging..."
                sh "/usr/local/bin/sbt package"
                }
            }
        }

    }
}