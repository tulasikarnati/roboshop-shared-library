#!groovy

def decidepipeline(Map, configMap) {
        application = configMap.get(application)
        switch(application) {
            case 'nodejsVM':
                nodejsVM(configMap)
            break
            case 'javaVM':
                javaVM(configMap)
            break
            case 'nodejsEKs':
                nodejsEKs(configMap)
            break
            default:
                error "Application is not recognized"
            break
        }
}