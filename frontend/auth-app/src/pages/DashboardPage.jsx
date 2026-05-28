import { useCallback, useEffect, useMemo, useState } from 'react'
import { getApiMessage } from '../api/httpClient'
import {
  createExam,
  deleteExam,
  getExamDetail,
  getExams,
  updateExam,
} from '../api/examApi'
import {
  createQuestion,
  deleteQuestion,
  importQuestions,
  updateQuestion,
} from '../api/questionApi'
import { changePassword } from '../api/userApi'

const emptyExamForm = {
  title: '',
  duration: 30,
}

const emptyQuestionForm = {
  questionText: '',
  answer1: '',
  answer2: '',
  answer3: '',
  answer4: '',
  correctAnswer: 1,
}

function DashboardPage({ onLogout, onRefreshSession, isRefreshingSession }) {
  const [exams, setExams] = useState([])
  const [selectedExam, setSelectedExam] = useState(null)
  const [examForm, setExamForm] = useState(emptyExamForm)
  const [questionForm, setQuestionForm] = useState(emptyQuestionForm)
  const [questionsByExam, setQuestionsByExam] = useState({})
  const [editingQuestionId, setEditingQuestionId] = useState(null)
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: '',
  })
  const [importFile, setImportFile] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isSavingExam, setIsSavingExam] = useState(false)
  const [isSavingQuestion, setIsSavingQuestion] = useState(false)
  const [isImporting, setIsImporting] = useState(false)
  const [isChangingPassword, setIsChangingPassword] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const selectedExamQuestions = useMemo(() => {
    if (!selectedExam) {
      return []
    }

    return questionsByExam[selectedExam.id] || []
  }, [questionsByExam, selectedExam])

  const totalQuestions = Object.values(questionsByExam).reduce(
    (total, questions) => total + questions.length,
    0,
  )

  const showError = useCallback((err, fallbackMessage) => {
    setSuccess('')
    setError(getApiMessage(err, fallbackMessage))
  }, [])

  const showSuccess = (message) => {
    setError('')
    setSuccess(message)
  }

  const selectExam = useCallback(async (examId) => {
    setError('')

    try {
      const exam = await getExamDetail(examId)
      setSelectedExam(exam)
      setExamForm({
        title: exam?.title || '',
        duration: exam?.duration || 30,
      })
      setQuestionForm(emptyQuestionForm)
      setEditingQuestionId(null)
    } catch (err) {
      showError(err, 'Unable to load exam detail.')
    }
  }, [showError])

  const loadExams = useCallback(async () => {
    setIsLoading(true)
    setError('')

    try {
      const data = await getExams()
      setExams(data)

      const nextSelectedId = data.some((exam) => exam.id === selectedExam?.id)
        ? selectedExam.id
        : data[0]?.id

      if (nextSelectedId) {
        await selectExam(nextSelectedId)
      } else {
        setSelectedExam(null)
        setExamForm(emptyExamForm)
      }
    } catch (err) {
      showError(err, 'Unable to load exams.')
    } finally {
      setIsLoading(false)
    }
  }, [selectedExam, selectExam, showError])

  useEffect(() => {
    const timerId = window.setTimeout(() => {
      loadExams()
    }, 0)

    return () => window.clearTimeout(timerId)
  }, [loadExams])

  const updateExamForm = (event) => {
    const { name, value } = event.target
    setExamForm((current) => ({
      ...current,
      [name]: name === 'duration' ? Number(value) : value,
    }))
  }

  const updateQuestionForm = (event) => {
    const { name, value } = event.target
    setQuestionForm((current) => ({
      ...current,
      [name]: name === 'correctAnswer' ? Number(value) : value,
    }))
  }

  const handleCreateExam = async (event) => {
    event.preventDefault()

    if (!examForm.title.trim() || examForm.duration <= 0) {
      setError('Please enter an exam title and a positive duration.')
      return
    }

    setIsSavingExam(true)

    try {
      const exam = await createExam({
        title: examForm.title.trim(),
        duration: examForm.duration,
      })
      setExams((current) => [exam, ...current])
      setSelectedExam(exam)
      setExamForm({ title: exam.title, duration: exam.duration })
      showSuccess('Exam created successfully.')
    } catch (err) {
      showError(err, 'Unable to create exam.')
    } finally {
      setIsSavingExam(false)
    }
  }

  const handleUpdateExam = async () => {
    if (!selectedExam) {
      return
    }

    if (!examForm.title.trim() || examForm.duration <= 0) {
      setError('Please enter an exam title and a positive duration.')
      return
    }

    setIsSavingExam(true)

    try {
      const exam = await updateExam(selectedExam.id, {
        title: examForm.title.trim(),
        duration: examForm.duration,
      })
      setSelectedExam(exam)
      setExams((current) =>
        current.map((item) => (item.id === exam.id ? exam : item)),
      )
      showSuccess('Exam updated successfully.')
    } catch (err) {
      showError(err, 'Unable to update exam.')
    } finally {
      setIsSavingExam(false)
    }
  }

  const handleDeleteExam = async () => {
    if (!selectedExam) {
      return
    }

    setIsSavingExam(true)

    try {
      await deleteExam(selectedExam.id)
      const remainingExams = exams.filter((exam) => exam.id !== selectedExam.id)
      setExams(remainingExams)
      setQuestionsByExam((current) => {
        const next = { ...current }
        delete next[selectedExam.id]
        return next
      })
      setSelectedExam(null)
      setExamForm(emptyExamForm)

      if (remainingExams.length) {
        await selectExam(remainingExams[0].id)
      }

      showSuccess('Exam deleted successfully.')
    } catch (err) {
      showError(err, 'Unable to delete exam.')
    } finally {
      setIsSavingExam(false)
    }
  }

  const resetQuestionForm = () => {
    setQuestionForm(emptyQuestionForm)
    setEditingQuestionId(null)
  }

  const handleSubmitQuestion = async (event) => {
    event.preventDefault()

    if (!selectedExam) {
      setError('Please select an exam first.')
      return
    }

    const hasMissingField = [
      questionForm.questionText,
      questionForm.answer1,
      questionForm.answer2,
      questionForm.answer3,
      questionForm.answer4,
    ].some((value) => !value.trim())

    if (hasMissingField) {
      setError('Please fill in the question and all four answers.')
      return
    }

    setIsSavingQuestion(true)

    try {
      const payload = {
        ...questionForm,
        questionText: questionForm.questionText.trim(),
        answer1: questionForm.answer1.trim(),
        answer2: questionForm.answer2.trim(),
        answer3: questionForm.answer3.trim(),
        answer4: questionForm.answer4.trim(),
      }
      const savedQuestion = editingQuestionId
        ? await updateQuestion(selectedExam.id, editingQuestionId, payload)
        : await createQuestion(selectedExam.id, payload)

      setQuestionsByExam((current) => {
        const currentQuestions = current[selectedExam.id] || []
        const nextQuestions = editingQuestionId
          ? currentQuestions.map((question) =>
              question.id === savedQuestion.id ? savedQuestion : question,
            )
          : [savedQuestion, ...currentQuestions]

        return {
          ...current,
          [selectedExam.id]: nextQuestions,
        }
      })
      resetQuestionForm()
      showSuccess(editingQuestionId ? 'Question updated.' : 'Question created.')
    } catch (err) {
      showError(err, 'Unable to save question.')
    } finally {
      setIsSavingQuestion(false)
    }
  }

  const startEditingQuestion = (question) => {
    setEditingQuestionId(question.id)
    setQuestionForm({
      questionText: question.questionText,
      answer1: question.answer1,
      answer2: question.answer2,
      answer3: question.answer3,
      answer4: question.answer4,
      correctAnswer: question.correctAnswer,
    })
  }

  const handleDeleteQuestion = async (questionId) => {
    if (!selectedExam) {
      return
    }

    setIsSavingQuestion(true)

    try {
      await deleteQuestion(selectedExam.id, questionId)
      setQuestionsByExam((current) => ({
        ...current,
        [selectedExam.id]: (current[selectedExam.id] || []).filter(
          (question) => question.id !== questionId,
        ),
      }))
      showSuccess('Question deleted.')
    } catch (err) {
      showError(err, 'Unable to delete question.')
    } finally {
      setIsSavingQuestion(false)
    }
  }

  const handleImportQuestions = async (event) => {
    event.preventDefault()

    if (!selectedExam) {
      setError('Please select an exam first.')
      return
    }

    if (!importFile) {
      setError('Please choose an Excel file to import.')
      return
    }

    setIsImporting(true)

    try {
      const importedQuestions = await importQuestions(selectedExam.id, importFile)
      setQuestionsByExam((current) => ({
        ...current,
        [selectedExam.id]: [
          ...importedQuestions,
          ...(current[selectedExam.id] || []),
        ],
      }))
      setImportFile(null)
      event.target.reset()
      showSuccess(`${importedQuestions.length} questions imported.`)
    } catch (err) {
      showError(err, 'Unable to import questions.')
    } finally {
      setIsImporting(false)
    }
  }

  const handleChangePassword = async (event) => {
    event.preventDefault()

    if (!passwordForm.oldPassword || passwordForm.newPassword.length < 6) {
      setError('New password must be at least 6 characters.')
      return
    }

    setIsChangingPassword(true)

    try {
      await changePassword(passwordForm)
      setPasswordForm({ oldPassword: '', newPassword: '' })
      showSuccess('Password updated successfully.')
    } catch (err) {
      showError(err, 'Unable to update password.')
    } finally {
      setIsChangingPassword(false)
    }
  }

  return (
    <main className="app-bg min-h-screen text-slate-950">
      <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-5 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8">
          <div>
            <p className="text-sm font-semibold text-teal-700">Quiz Online</p>
            <h1 className="mt-1 text-2xl font-bold tracking-normal sm:text-3xl">
              Exam Management Dashboard
            </h1>
            <p className="mt-2 text-sm text-slate-600">
              Build exams, manage questions, and import Excel question banks.
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <button
              type="button"
              onClick={onRefreshSession}
              disabled={isRefreshingSession}
              className="btn btn-secondary"
            >
              {isRefreshingSession ? 'Refreshing...' : 'Refresh session'}
            </button>
            <button
              type="button"
              onClick={onLogout}
              className="btn btn-dark"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto grid max-w-7xl gap-6 px-4 py-6 sm:px-6 lg:grid-cols-[280px_minmax(0,1fr)] lg:px-8">
        <aside className="space-y-4">
          <section className="surface-card rounded-xl p-4">
            <p className="text-sm font-semibold text-slate-500">Overview</p>
            <div className="mt-4 grid grid-cols-3 gap-3 lg:grid-cols-1">
              <StatCard label="Exams" value={exams.length} />
              <StatCard label="Questions" value={totalQuestions} />
              <StatCard label="Selected" value={selectedExam ? selectedExam.id : '-'} />
            </div>
          </section>

          <section className="surface-card rounded-xl p-4">
            <div className="flex items-center justify-between">
              <h2 className="text-sm font-semibold text-slate-950">My Exams</h2>
              <button
                type="button"
                onClick={loadExams}
                disabled={isLoading}
                className="rounded-md px-2 py-1 text-sm font-semibold text-teal-700 transition hover:bg-teal-50 hover:text-teal-900 disabled:text-slate-400"
              >
                {isLoading ? 'Loading' : 'Reload'}
              </button>
            </div>

            <div className="mt-4 space-y-2">
              {exams.length ? (
                exams.map((exam) => (
                  <button
                    key={exam.id}
                    type="button"
                    onClick={() => selectExam(exam.id)}
                    className={`interactive-card w-full rounded-lg border px-3 py-3 text-left ${
                      selectedExam?.id === exam.id
                        ? 'border-teal-600 bg-teal-50 shadow-sm shadow-teal-950/5'
                        : 'border-slate-200 bg-white hover:border-teal-200 hover:bg-teal-50/50'
                    }`}
                  >
                    <span className="block text-sm font-semibold text-slate-950">
                      {exam.title}
                    </span>
                    <span className="mt-1 block text-xs text-slate-500">
                      {exam.duration} minutes
                    </span>
                  </button>
                ))
              ) : (
                <p className="rounded-md border border-dashed border-slate-300 p-4 text-sm text-slate-500">
                  No exams yet. Create your first quiz from the main panel.
                </p>
              )}
            </div>
          </section>
        </aside>

        <section className="space-y-6">
          {(error || success) && (
            <div
              className={`toast-card rounded-xl border px-4 py-3 text-sm font-medium shadow-sm ${
                error
                  ? 'border-red-200 bg-red-50 text-red-700'
                  : 'border-teal-200 bg-teal-50 text-teal-800'
              }`}
            >
              {error || success}
            </div>
          )}

          <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_340px]">
            <section className="surface-card rounded-xl p-5">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <p className="text-sm font-semibold text-teal-700">Exam CRUD</p>
                  <h2 className="mt-1 text-xl font-semibold tracking-normal">
                    {selectedExam ? selectedExam.title : 'Create a new exam'}
                  </h2>
                </div>
                {selectedExam && (
                  <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                    Exam #{selectedExam.id}
                  </span>
                )}
              </div>

              <form className="mt-5 grid gap-4 sm:grid-cols-[minmax(0,1fr)_160px]" onSubmit={handleCreateExam}>
                <Field label="Title" htmlFor="exam-title">
                  <input
                    id="exam-title"
                    name="title"
                    value={examForm.title}
                    onChange={updateExamForm}
                    placeholder="Java Basic Quiz"
                    className="input-control mt-2"
                  />
                </Field>

                <Field label="Duration" htmlFor="exam-duration">
                  <input
                    id="exam-duration"
                    name="duration"
                    type="number"
                    min="1"
                    value={examForm.duration}
                    onChange={updateExamForm}
                    className="input-control mt-2"
                  />
                </Field>

                <div className="flex flex-wrap gap-3 sm:col-span-2">
                  <button
                    type="submit"
                    disabled={isSavingExam}
                    className="btn btn-primary"
                  >
                    {isSavingExam ? 'Saving...' : 'Create exam'}
                  </button>
                  <button
                    type="button"
                    onClick={handleUpdateExam}
                    disabled={!selectedExam || isSavingExam}
                    className="btn btn-secondary"
                  >
                    Update selected
                  </button>
                  <button
                    type="button"
                    onClick={handleDeleteExam}
                    disabled={!selectedExam || isSavingExam}
                    className="btn btn-danger"
                  >
                    Delete selected
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setSelectedExam(null)
                      setExamForm(emptyExamForm)
                    }}
                    className="btn btn-ghost"
                  >
                    Clear form
                  </button>
                </div>
              </form>
            </section>

            <section className="surface-card rounded-xl p-5">
              <p className="text-sm font-semibold text-teal-700">Account</p>
              <h2 className="mt-1 text-lg font-semibold tracking-normal">
                Change password
              </h2>
              <form className="mt-5 space-y-4" onSubmit={handleChangePassword}>
                <Field label="Old password" htmlFor="old-password">
                  <input
                    id="old-password"
                    type="password"
                    value={passwordForm.oldPassword}
                    onChange={(event) =>
                      setPasswordForm((current) => ({
                        ...current,
                        oldPassword: event.target.value,
                      }))
                    }
                    className="input-control mt-2"
                  />
                </Field>
                <Field label="New password" htmlFor="new-password">
                  <input
                    id="new-password"
                    type="password"
                    value={passwordForm.newPassword}
                    onChange={(event) =>
                      setPasswordForm((current) => ({
                        ...current,
                        newPassword: event.target.value,
                      }))
                    }
                    className="input-control mt-2"
                  />
                </Field>
                <button
                  type="submit"
                  disabled={isChangingPassword}
                  className="btn btn-dark w-full"
                >
                  {isChangingPassword ? 'Updating...' : 'Update password'}
                </button>
              </form>
            </section>
          </div>

          <section className="surface-card rounded-xl p-5">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-teal-700">Question CRUD</p>
                <h2 className="mt-1 text-xl font-semibold tracking-normal">
                  {selectedExam ? `Questions for ${selectedExam.title}` : 'Select an exam'}
                </h2>
                <p className="mt-2 text-sm text-slate-500">
                  The API document does not include a list-questions endpoint, so new
                  and imported questions are shown in this session after saving.
                </p>
              </div>
            </div>

            <div className="mt-6 grid gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
              <form className="space-y-4" onSubmit={handleSubmitQuestion}>
                <Field label="Question text" htmlFor="question-text">
                  <textarea
                    id="question-text"
                    name="questionText"
                    value={questionForm.questionText}
                    onChange={updateQuestionForm}
                    rows="3"
                    disabled={!selectedExam}
                    className="input-control mt-2"
                    placeholder="Java is a programming language?"
                  />
                </Field>

                <div className="grid gap-4 md:grid-cols-2">
                  {[1, 2, 3, 4].map((index) => (
                    <Field key={index} label={`Answer ${index}`} htmlFor={`answer-${index}`}>
                      <input
                        id={`answer-${index}`}
                        name={`answer${index}`}
                        value={questionForm[`answer${index}`]}
                        onChange={updateQuestionForm}
                        disabled={!selectedExam}
                        className="input-control mt-2"
                      />
                    </Field>
                  ))}
                </div>

                <Field label="Correct answer" htmlFor="correct-answer">
                  <select
                    id="correct-answer"
                    name="correctAnswer"
                    value={questionForm.correctAnswer}
                    onChange={updateQuestionForm}
                    disabled={!selectedExam}
                    className="input-control mt-2"
                  >
                    <option value={1}>Answer 1</option>
                    <option value={2}>Answer 2</option>
                    <option value={3}>Answer 3</option>
                    <option value={4}>Answer 4</option>
                  </select>
                </Field>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="submit"
                    disabled={!selectedExam || isSavingQuestion}
                    className="btn btn-primary"
                  >
                    {isSavingQuestion
                      ? 'Saving...'
                      : editingQuestionId
                        ? 'Update question'
                        : 'Create question'}
                  </button>
                  <button
                    type="button"
                    onClick={resetQuestionForm}
                    className="btn btn-secondary"
                  >
                    Reset
                  </button>
                </div>
              </form>

              <aside className="space-y-5">
                <form
                  className="interactive-card rounded-xl border border-dashed border-slate-300 bg-slate-50 p-4"
                  onSubmit={handleImportQuestions}
                >
                  <h3 className="text-sm font-semibold text-slate-950">
                    Import Excel questions
                  </h3>
                  <p className="mt-2 text-sm leading-6 text-slate-500">
                    Upload an .xlsx file with Question Text, Answer 1-4, and Correct
                    Answer columns.
                  </p>
                  <input
                    type="file"
                    accept=".xlsx"
                    disabled={!selectedExam}
                    onChange={(event) => setImportFile(event.target.files?.[0] || null)}
                    className="input-control mt-4"
                  />
                  <button
                    type="submit"
                    disabled={!selectedExam || !importFile || isImporting}
                    className="btn btn-dark mt-4 w-full"
                  >
                    {isImporting ? 'Importing...' : 'Import questions'}
                  </button>
                </form>

                <div className="rounded-xl border border-slate-200 bg-white">
                  <div className="border-b border-slate-200 px-4 py-3">
                    <h3 className="text-sm font-semibold text-slate-950">
                      Session questions
                    </h3>
                  </div>
                  <div className="max-h-[420px] space-y-3 overflow-auto p-4">
                    {selectedExamQuestions.length ? (
                      selectedExamQuestions.map((question) => (
                        <article
                          key={question.id}
                          className="interactive-card rounded-lg border border-slate-200 bg-white p-3"
                        >
                          <p className="text-sm font-semibold text-slate-950">
                            {question.questionText}
                          </p>
                          <ol className="mt-3 space-y-1 text-sm text-slate-600">
                            {[1, 2, 3, 4].map((index) => (
                              <li
                                key={index}
                                className={
                                  question.correctAnswer === index
                                    ? 'font-semibold text-teal-700'
                                    : ''
                                }
                              >
                                {index}. {question[`answer${index}`]}
                              </li>
                            ))}
                          </ol>
                          <div className="mt-3 flex gap-2">
                            <button
                              type="button"
                              onClick={() => startEditingQuestion(question)}
                              className="rounded-md border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 transition hover:-translate-y-0.5 hover:border-slate-400 hover:bg-slate-100"
                            >
                              Edit
                            </button>
                            <button
                              type="button"
                              onClick={() => handleDeleteQuestion(question.id)}
                              disabled={isSavingQuestion}
                              className="rounded-md border border-red-200 px-3 py-1.5 text-xs font-semibold text-red-700 transition hover:-translate-y-0.5 hover:border-red-300 hover:bg-red-50 disabled:text-slate-400"
                            >
                              Delete
                            </button>
                          </div>
                        </article>
                      ))
                    ) : (
                      <p className="text-sm leading-6 text-slate-500">
                        No local questions yet. Create or import questions for the
                        selected exam.
                      </p>
                    )}
                  </div>
                </div>
              </aside>
            </div>
          </section>
        </section>
      </div>
    </main>
  )
}

function Field({ children, htmlFor, label }) {
  return (
    <label className="block text-sm font-medium text-slate-700" htmlFor={htmlFor}>
      {label}
      {children}
    </label>
  )
}

function StatCard({ label, value }) {
  return (
    <div className="interactive-card rounded-lg border border-slate-200 bg-slate-50 p-3">
      <p className="text-xs font-semibold uppercase tracking-normal text-slate-500">
        {label}
      </p>
      <p className="mt-2 text-2xl font-bold tracking-normal text-slate-950">
        {value}
      </p>
    </div>
  )
}

export default DashboardPage
