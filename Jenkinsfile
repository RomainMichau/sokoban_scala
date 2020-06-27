/* uses sbt, which i installed with homebrew. */
/* this works without requiring the 'sbt plugin'. */

pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                sh "/usr/local/bin/sbt compile --no-color"
            }
        }

        stage('Lint') {
            steps {
                echo "Testing..."
                sh "/usr/local/bin/sbt scalastyle -scalastyleFailOnWarning --no-color"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                sh "/usr/local/bin/sbt test --no-color"
            }
        }


        stage('Package') {
            steps {
                echo "Packaging..."
                sh "/usr/local/bin/sbt package --no-color"
            }
        }

    }
}