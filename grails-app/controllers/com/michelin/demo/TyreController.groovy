package com.michelin.demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TyreController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Tyre.list(params), model: [tyreCount: Tyre.count()]
    }

    def show(Tyre tyre) {
        respond tyre
    }

    def create() {
        respond new Tyre(params)
    }

    @Transactional
    def save(Tyre tyre) {
        if (tyre == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (tyre.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond tyre.errors, view: 'create'
            return
        }

        tyre.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'tyre.label', default: 'Tyre'), tyre.id])
                redirect tyre
            }
            '*' { respond tyre, [status: CREATED] }
        }
    }

    def edit(Tyre tyre) {
        respond tyre
    }

    @Transactional
    def update(Tyre tyre) {
        if (tyre == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (tyre.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond tyre.errors, view: 'edit'
            return
        }

        tyre.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'tyre.label', default: 'Tyre'), tyre.id])
                redirect tyre
            }
            '*' { respond tyre, [status: OK] }
        }
    }

    @Transactional
    def delete(Tyre tyre) {

        if (tyre == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        tyre.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'tyre.label', default: 'Tyre'), tyre.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'tyre.label', default: 'Tyre'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
