package models

class Create_Post extends RestBaseModel {
    Create_Post(){
        url = "/create"
        method = "post"

        headers = ['Accept':'application/json']

        body = '{"name":""}'
    }
}
