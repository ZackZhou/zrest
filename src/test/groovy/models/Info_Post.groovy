package models

class Info_Post extends RestBaseModel {
    Info_Post() {
        url = "/info"
        method = "post"

        headers = ['Accept': 'application/json']
    }
}
