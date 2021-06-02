package models

class List_Get extends RestBaseModel {
    List_Get() {
        url = "/list"
        method = "get"

        headers = ['Accept': 'application/json']
    }
}