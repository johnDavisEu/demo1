package com.michelin.demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TotoController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Toto.list(params), model: [totoCount: Toto.count()]
    }

    def show(Toto toto) {
        respond toto
    }

    def create() {
        respond new Toto(params)
    }

    @Transactional
    def save(Toto toto) {
        if (toto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (toto.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond toto.errors, view: 'create'
            return
        }

        toto.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'toto.label', default: 'Toto'), toto.id])
                redirect toto
            }
            '*' { respond toto, [status: CREATED] }
        }
    }

    def edit(Toto toto) {
        respond toto
    }

    @Transactional
    def update(Toto toto) {
        if (toto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (toto.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond toto.errors, view: 'edit'
            return
        }

        toto.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'toto.label', default: 'Toto'), toto.id])
                redirect toto
            }
            '*' { respond toto, [status: OK] }
        }
    }

    @Transactional
    def delete(Toto toto) {

        if (toto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        toto.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'toto.label', default: 'Toto'), toto.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'toto.label', default: 'Toto'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
