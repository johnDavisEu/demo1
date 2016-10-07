package com.michelin.demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NormController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Norm.list(params), model: [normCount: Norm.count()]
    }

    def show(Norm norm) {
        respond norm
    }

    def create() {
        respond new Norm(params)
    }

    @Transactional
    def save(Norm norm) {
        if (norm == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (norm.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond norm.errors, view: 'create'
            return
        }

        norm.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'norm.label', default: 'Norm'), norm.id])
                redirect norm
            }
            '*' { respond norm, [status: CREATED] }
        }
    }

    def edit(Norm norm) {
        respond norm
    }

    @Transactional
    def update(Norm norm) {
        if (norm == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (norm.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond norm.errors, view: 'edit'
            return
        }

        norm.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'norm.label', default: 'Norm'), norm.id])
                redirect norm
            }
            '*' { respond norm, [status: OK] }
        }
    }

    @Transactional
    def delete(Norm norm) {

        if (norm == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        norm.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'norm.label', default: 'Norm'), norm.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'norm.label', default: 'Norm'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
