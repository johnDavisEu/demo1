package com.michelin.demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ComputerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Computer.list(params), model: [computerCount: Computer.count()]
    }

    def show(Computer computer) {
        respond computer
    }

    def create() {
        respond new Computer(params)
    }

    @Transactional
    def save(Computer computer) {
        if (computer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (computer.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond computer.errors, view: 'create'
            return
        }

        computer.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'computer.label', default: 'Computer'), computer.id])
                redirect computer
            }
            '*' { respond computer, [status: CREATED] }
        }
    }

    def edit(Computer computer) {
        respond computer
    }

    @Transactional
    def update(Computer computer) {
        if (computer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (computer.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond computer.errors, view: 'edit'
            return
        }

        computer.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'computer.label', default: 'Computer'), computer.id])
                redirect computer
            }
            '*' { respond computer, [status: OK] }
        }
    }

    @Transactional
    def delete(Computer computer) {

        if (computer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        computer.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'computer.label', default: 'Computer'), computer.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'computer.label', default: 'Computer'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
