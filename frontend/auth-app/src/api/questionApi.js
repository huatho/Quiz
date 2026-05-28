import httpClient from './httpClient'
import { unwrapApiResponse } from './apiResponse'

export async function createQuestion(examId, payload) {
  const response = await httpClient.post(`/exams/${examId}/questions`, payload)
  return unwrapApiResponse(response)
}

export async function importQuestions(examId, file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await httpClient.post(`/exams/${examId}/questions/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return unwrapApiResponse(response) || []
}

export async function updateQuestion(examId, questionId, payload) {
  const response = await httpClient.put(
    `/exams/${examId}/questions/${questionId}`,
    payload,
  )
  return unwrapApiResponse(response)
}

export async function deleteQuestion(examId, questionId) {
  const response = await httpClient.delete(`/exams/${examId}/questions/${questionId}`)
  return unwrapApiResponse(response)
}
