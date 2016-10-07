package com.michelin.demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DemoController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Demo.list(params), model: [demoCount: Demo.count()]
    }

    def show(Demo demo) {
        respond demo
    }

    def create() {
        respond new Demo(params)
    }

    @Transactional
    def save(Demo demo) {
        if (demo == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (demo.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond demo.errors, view: 'create'
            return
        }

        demo.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'demo.label', default: 'Demo'), demo.id])
                redirect demo
            }
            '*' { respond demo, [status: CREATED] }
        }
    }

    def edit(Demo demo) {
        respond demo
    }

    @Transactional
    def update(Demo demo) {
        if (demo == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (demo.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond demo.errors, view: 'edit'
            return
        }

        demo.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'demo.label', default: 'Demo'), demo.id])
                redirect demo
            }
            '*' { respond demo, [status: OK] }
        }
    }

    @Transactional
    def delete(Demo demo) {

        if (demo == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        demo.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'demo.label', default: 'Demo'), demo.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'demo.label', default: 'Demo'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
