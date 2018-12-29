pipeline {
    agent any

    stages{
        stage("one"){
            steps{
                http url: "${url}"
            }
        }
    }
}